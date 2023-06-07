package no.nav.pensjon.selvbetjening.fssgw.sts

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(StsController::class)
internal class StsControllerTest {

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
    fun `access token request results in JWT access token response`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn(JWT_ACCESS_TOKEN_RESPONSE_BODY)
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(serviceTokenData())

        mvc.perform(
            get(JWT_ACCESS_TOKEN_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
            .andExpect(status().isOk)
            .andExpect(content().json(JWT_ACCESS_TOKEN_RESPONSE_BODY))
    }

    @Test
    fun `token exchange request results in SAML token response`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString()))
            .thenReturn(SAML_TOKEN_RESPONSE_BODY)
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(serviceTokenData())

        mvc.perform(
            post(TOKEN_EXCHANGE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content(SAML_TOKEN_REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(content().json(SAML_TOKEN_RESPONSE_BODY))
    }

    @Test
    fun `unauthorized JWT access token request results in response status Unauthorized`() {
        mvc.perform(
            get(JWT_ACCESS_TOKEN_PATH))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `unauthorized token exchange request results in response status Unauthorized`() {
        mvc.perform(
            post(TOKEN_EXCHANGE_PATH)
                .content(SAML_TOKEN_REQUEST_BODY))
            .andExpect(status().isUnauthorized)
    }

    private companion object {
        const val BASE_PATH = "/rest/v1/sts/token"
        const val TOKEN_EXCHANGE_PATH = "$BASE_PATH/exchange"
        const val JWT_ACCESS_TOKEN_PATH = "$BASE_PATH?grant_type=client_credentials&scope=openid"

        // access_token is a JWT access token
        @Language("json")
        private const val JWT_ACCESS_TOKEN_RESPONSE_BODY = """{
    "access_token": "eyJra...ehtzw",
    "token_type": "Bearer",
    "expires_in": 3600
}"""

        // subject_token is a JWT ID token
        private const val SAML_TOKEN_REQUEST_BODY = """grant_type: "urn:ietf:params:oauth:grant-type:token-exchange"
subject_token_type: "urn:ietf:params:oauth:token-type:access_token"
subject_token: "eyJra...PH5mA""""

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
