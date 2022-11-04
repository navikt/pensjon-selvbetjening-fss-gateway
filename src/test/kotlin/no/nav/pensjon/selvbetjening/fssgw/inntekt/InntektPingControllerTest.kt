package no.nav.pensjon.selvbetjening.fssgw.inntekt

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(InntektPingController::class)
internal class InntektPingControllerTest {

    private val credentials = "cred"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var authValidator: BasicAuthValidator

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then BehandleInntekt ping request responds with OK`() {
        `when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
            .thenReturn("""{ "response": "pong"}""")
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            MockMvcRequestBuilders.get("/inntektskomponenten-ws//inntekt/BehandleInntekt/ping")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "pong"}"""))
    }

    @Test
    fun `when OK then Inntektv3 ping request responds with OK`() {
        `when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
            .thenReturn("""{ "response": "pong"}""")
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            MockMvcRequestBuilders.get("/inntektskomponenten-ws/inntekt/v3/Inntekt/ping")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "pong"}"""))
    }
}
