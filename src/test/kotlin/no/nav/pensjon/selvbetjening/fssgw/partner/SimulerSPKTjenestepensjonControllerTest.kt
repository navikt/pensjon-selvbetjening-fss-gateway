package no.nav.pensjon.selvbetjening.fssgw.partner

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten.MaskinportenToken
import no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten.MaskinportenTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(SimulerSPKTjenestepensjonController::class)
class SimulerSPKTjenestepensjonControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Mock
    lateinit var claims: Claims

    @MockitoBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockitoBean
    lateinit var serviceClient: ServiceClient

    @MockitoBean
    lateinit var callIdGenerator: CallIdGenerator

    @MockitoBean
    lateinit var egressTokenGetter: MaskinportenTokenGetter

    @Test
    fun `when authenticated request then response is OK`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString(), anyBoolean())).thenReturn(RESPONSE_BODY)
        `when`(callIdGenerator.newCallId()).thenReturn("call ID 1")
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)
        val scope = "test:spk:nav"
        `when`(egressTokenGetter.getToken(scope)).thenReturn(MaskinportenToken("jwt", "Bearer", 1, scope))
        val expectedMediaType = MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString()

        mvc.perform(
            post(PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("scope", scope)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(REQUEST_BODY)
        )
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json(RESPONSE_BODY))

        verify(serviceClient, times(1)).doPost(
            "$BASE_URL$PATH",
            mapOf("Content-Type" to expectedMediaType, "Nav-Call-Id" to "call ID 1", HttpHeaders.AUTHORIZATION to "Bearer jwt"),
            REQUEST_BODY,
            true
        )
    }

    @Test
    fun `unauthorized simulation request results in response status Unauthorized`() {
        mvc.perform(
            post(PATH)
                .header("scope", "test:spk:nav")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(REQUEST_BODY)
        )
            .andExpect(status().isUnauthorized)
    }

    companion object {
        const val BASE_URL = "https://api.preprod.spk.no"
        const val PATH = "/medlem/pensjon/prognose/v1"
        const val REQUEST_BODY = """{"request":"body"}"""
        const val RESPONSE_BODY = """{"response":"body"}"""
    }
}
