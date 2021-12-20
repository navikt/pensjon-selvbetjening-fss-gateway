package no.nav.pensjon.selvbetjening.fssgw.kodeverk

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.anyObject
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

@WebMvcTest(KodeverkController::class)
internal class KodeverkControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `Kodeverk request results in JSON response`() {
        Mockito.`when`(serviceClient.callService(anyObject(), anyObject())).thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.get("/api/v1/kodeverk/Postnummer/koder/betydninger?spraak=nb")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Consumer-Id", "id")
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }
}
