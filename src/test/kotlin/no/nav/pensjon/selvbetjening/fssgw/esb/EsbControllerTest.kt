package no.nav.pensjon.selvbetjening.fssgw.esb

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
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

@WebMvcTest(EsbController::class)
internal class EsbControllerTest {

    private val utbetalingPath = "/nav-cons-pen-pselv-utbetalingWeb/sca/PSELVUtbetalingWSEXP"

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

    @Test
    fun `utbetaling request results in utbetaling response XML`() {
        doTest(utbetalingPath, EsbXml.utbetalingResponseBody)
    }

    private fun doTest(path: String, expectedResponseBody: String) {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString(), anyBoolean())).thenReturn(expectedResponseBody)
        `when`(callIdGenerator.newCallId()).thenReturn("call ID 1")
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)
        val expectedMediaType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8)

        mvc.perform(
            post(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content("""<?xml version="1.0" encoding="UTF-8"?><soapenv:Header><wsse:UsernameToken><wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">__password__</wsse:Password></wsse:UsernameToken></soapenv:Header><body><foo /></body>"""))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMediaType.toString()))
            .andExpect(content().xml(expectedResponseBody))

        verify(serviceClient, times(1)).doPost(
            "https://tjenestebuss-q2.adeo.no$path",
            mapOf("Content-Type" to "text/xml;charset=UTF-8", "Nav-Call-Id" to "call ID 1"),
            """<?xml version="1.0" encoding="UTF-8"?><soapenv:Header><wsse:UsernameToken><wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">&amp;secret</wsse:Password></wsse:UsernameToken></soapenv:Header><body><foo /></body>""")
    }
}
