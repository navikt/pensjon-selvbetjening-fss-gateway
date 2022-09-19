package no.nav.pensjon.selvbetjening.fssgw.inntekt

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(InntektController::class)
internal class InntektControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    private val auth = "Bearer jwt"

    @Test
    fun `when OK then ForventedeInntekter request returns data`() {
        Mockito.`when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
            .thenReturn("""{ "response": "ForventedeInntekter"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.get("/inntektskomponenten-ws/rs/api/v1/forventetinntekt")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789")
                .header("aar-list", "2020")
                .header("aar-list", "2021"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("""{"response": "ForventedeInntekter"}"""))
    }

    @Test
    fun `when OK then ForventedeInntekter post returns data`() {
        Mockito.`when`(serviceClient.doPost(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap(), ArgumentMatchers.anyString()))
            .thenReturn("""{ "response": "ForventedeInntekter"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.post("/inntektskomponenten-ws/rs/api/v1/forventetinntekt")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .content("body"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("""{"response": "ForventedeInntekter"}"""))
    }
}