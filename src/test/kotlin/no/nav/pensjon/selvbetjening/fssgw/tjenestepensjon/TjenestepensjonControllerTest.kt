package no.nav.pensjon.selvbetjening.fssgw.tjenestepensjon

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TjenestepensjonController::class)
class TjenestepensjonControllerTest {

    private val ingressAuth = "Bearer JWT1"

    @Autowired
    lateinit var mvc: MockMvc

    @Mock
    lateinit var claims: Claims

    @MockBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then haveYtelse request returns data`() {
        val egressToken = serviceTokenData()
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "value": true}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(egressToken)
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)
        `when`(callIdGenerator.newCallId()).thenReturn(CALL_ID)

        mvc.perform(
            get(PATH)
                .header(HttpHeaders.AUTHORIZATION, ingressAuth)
                .header("pid", PID))
            .andExpect(status().isOk)
            .andExpect(content().json("""{ "value": true}"""))

        verify(serviceClient, times(1)).doGet(
            URL,
            mapOf(
                HttpHeaders.AUTHORIZATION to "Bearer ${egressToken.accessToken}",
                "pid" to PID,
                "Nav-Call-Id" to CALL_ID))
    }

    @Test
    fun `when no Authorization in request then response is Unauthorized`() {
        `when`(callIdGenerator.newCallId()).thenReturn(CALL_ID)

        mvc.perform(
            get(PATH)
                .header("pid", PID))
            .andExpect(status().isUnauthorized)
            .andExpect(content().string("Unauthorized"))

        verify(serviceClient, never()).doGet(anyString(), anyMap())
    }

    companion object {
        private const val PID = "01023456789"
        private const val CALL_ID = "call ID 1"
        private const val PATH = "/api/tjenestepensjon/haveYtelse"
        private const val URL = "https://tp-q2.dev.intern.nav.no$PATH"
    }
}
