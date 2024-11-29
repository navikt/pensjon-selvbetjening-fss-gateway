package no.nav.pensjon.selvbetjening.fssgw.sporing

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SporingsloggController::class)
internal class SporingsloggControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Mock
    lateinit var claims: Claims

    @MockitoBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockitoBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockitoBean
    lateinit var serviceClient: ServiceClient

    @MockitoBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `handlePostRequest request results in JSON response`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString(), anyBoolean())).thenReturn(RESPONSE_BODY)
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 2)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            post(SPORINGSLOGG_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(content().json(RESPONSE_BODY))
    }

    @Test
    fun `unauthorized request results in response status Unauthorized`() {
        mvc.perform(
            post(SPORINGSLOGG_PATH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isUnauthorized)
    }

    private companion object {
        const val SPORINGSLOGG_PATH = "/sporingslogg"

        const val REQUEST_BODY = """{
    "person": "12906498357",
    "mottaker": "889640782",
    "tema": "ABC",
    "behandlingsGrunnlag": "hjemmelen",
    "uthentingsTidspunkt": "2024-06-11T14:08:01.234",
    "leverteData": "bla bla",
    "samtykkeToken": null
}"""

        const val RESPONSE_BODY = """{}"""
    }
}
