package no.nav.pensjon.selvbetjening.fssgw.inntekt.ws

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
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(InntektWebServiceController::class)
internal class InntektWebServiceControllerTest {

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
    fun `inntekt request results in inntekt response XML`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString())).thenReturn(RESPONSE_BODY)
        `when`(egressTokenGetter.getServiceUserToken(useServiceUser2 = false)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)
        val expectedMediaType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8)

        mvc.perform(
            post(INNTEKT_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMediaType.toString()))
            .andExpect(content().xml(RESPONSE_BODY))
    }

    @Test
    fun `unauthorized inntekt request results in response status Unauthorized`() {
        mvc.perform(
            post(INNTEKT_PATH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `behandleInntekt request results in behandleInntekt response XML`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString())).thenReturn(RESPONSE_BODY)
        `when`(egressTokenGetter.getServiceUserToken(useServiceUser2 = false)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)
        val expectedMediaType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8)

        mvc.perform(
            post(BEHANDLE_INNTEKT_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMediaType.toString()))
            .andExpect(content().xml(RESPONSE_BODY))
    }

    @Test
    fun `unauthorized behandleInntekt request results in response status Unauthorized`() {
        mvc.perform(
            post(BEHANDLE_INNTEKT_PATH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isUnauthorized)
    }

    private companion object {
        const val INNTEKT_PATH = "/inntektskomponenten-ws/inntekt/v3/Inntekt"
        const val BEHANDLE_INNTEKT_PATH = "/inntektskomponenten-ws/inntekt/BehandleInntekt"

        const val REQUEST_BODY: String = """<?xml version="1.0" ?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header xmlns="http://www.w3.org/2005/08/addressing">
    </soapenv:Header>
    <soapenv:Body>
    </soapenv:Body>
</soapenv:Envelope>"""

        const val RESPONSE_BODY: String =
            """<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Body>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>"""
    }
}
