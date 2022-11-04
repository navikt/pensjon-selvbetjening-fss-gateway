package no.nav.pensjon.selvbetjening.fssgw.inntekt

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
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

@WebMvcTest(InntektPingRsController::class)
internal class InntektPingRsControllerTest {

    private val auth = "Bearer jwt"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then Inntekt ping request responds with OK`() {
        `when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
            .thenReturn("""{ "response": "pong"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.get("/inntektskomponenten-ws/rs/api/ping")
                .header(HttpHeaders.AUTHORIZATION, auth))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "pong"}"""))
    }
}
