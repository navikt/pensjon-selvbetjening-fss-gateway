package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.anyObject
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(AaregController::class)
internal class AaregControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `Aareg request results in JSON response`() {
        val expectedIdent = "00000000000"
        Mockito.`when`(serviceClient.doGet(anyObject(), anyObject())).thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders
                .get("/aareg-services/api/v1/arbeidstaker/arbeidsforhold")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personident", expectedIdent)
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("""{ "response": "bar"}"""))
    }
}
