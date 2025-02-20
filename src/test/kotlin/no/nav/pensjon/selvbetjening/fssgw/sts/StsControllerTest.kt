package no.nav.pensjon.selvbetjening.fssgw.sts

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(StsController::class)
internal class StsControllerTest {

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
    fun `access token request results in JWT access token response`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn(JWT_ACCESS_TOKEN_RESPONSE_BODY)
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get(JWT_ACCESS_TOKEN_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
            .andExpect(status().isOk)
            .andExpect(content().json(JWT_ACCESS_TOKEN_RESPONSE_BODY))
    }

    @Test
    fun `unauthorized JWT access token request results in response status Unauthorized`() {
        mvc.perform(
            get(JWT_ACCESS_TOKEN_PATH))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `token exchange request results in SAML token response`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString(), anyBoolean())).thenReturn(SAML_TOKEN_RESPONSE_BODY)
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            MockMvcRequestBuilders.post(TOKEN_EXCHANGE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content(SAML_TOKEN_REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(content().json(SAML_TOKEN_RESPONSE_BODY))
    }

    @Test
    fun `token exchange request with service-user-ID results in SAML token response`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString(), anyBoolean())).thenReturn(SAML_TOKEN_RESPONSE_BODY)
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            MockMvcRequestBuilders.post("$TOKEN_EXCHANGE_PATH?serviceUserId=2")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content(SAML_TOKEN_REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(content().json(SAML_TOKEN_RESPONSE_BODY))
    }

    @Test
    fun `unauthorized token exchange request results in response status Unauthorized`() {
        mvc.perform(
            MockMvcRequestBuilders.post(TOKEN_EXCHANGE_PATH)
                .content(SAML_TOKEN_REQUEST_BODY))
            .andExpect(status().isUnauthorized)
    }


    private companion object {
        const val BASE_PATH = "/rest/v1/sts/token"
        const val JWT_ACCESS_TOKEN_PATH = "$BASE_PATH?grant_type=client_credentials&scope=openid"
        const val TOKEN_EXCHANGE_PATH = "$BASE_PATH/exchange"

        // subject_token is a JWT ID token
        private const val SAML_TOKEN_REQUEST_BODY = "grant_type=urn:ietf:params:oauth:grant-type:token-exchange" +
                "&subject_token_type=urn:ietf:params:oauth:token-type:access_token" +
                "&subject_token=eyJra...PH5mA"

        // access_token is a JWT access token
        @Language("json")
        private const val JWT_ACCESS_TOKEN_RESPONSE_BODY = """{
    "access_token": "eyJra...ehtzw",
    "token_type": "Bearer",
    "expires_in": 3600
}"""

        // access_token is a Base64-encoded SAML token
        @Language("json")
        private const val SAML_TOKEN_RESPONSE_BODY = """{
    "access_token": "PHNhb...lvbj4",
    "issued_token_type": "urn:ietf:params:oauth:token-type:saml2",
    "token_type": "Bearer",
    "expires_in": 2649
}"""
    }
}
