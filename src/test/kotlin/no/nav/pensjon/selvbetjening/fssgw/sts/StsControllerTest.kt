package no.nav.pensjon.selvbetjening.fssgw.sts

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StsController::class)
class StsControllerTest : FunSpec() {

    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockkBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockkBean
    lateinit var serviceClient: ServiceClient

    @MockkBean
    lateinit var callIdGenerator: CallIdGenerator

    init {
        beforeSpec {
            every { callIdGenerator.newCallId() } returns "call ID 1"
        }

        test("access token request results in JWT access token response") {
            every { ingressTokenValidator.validate(any()) } returns mockk(relaxed = true)
            every { serviceClient.doGet(any(), any()) } returns JWT_ACCESS_TOKEN_RESPONSE_BODY
            every { egressTokenGetter.getServiceUserToken(serviceUserId = 1) } returns serviceTokenData()

            mvc.perform(
                get(JWT_ACCESS_TOKEN_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
            )
                .andExpect(status().isOk)
                .andExpect(content().json(JWT_ACCESS_TOKEN_RESPONSE_BODY))
        }

        test("unauthorized JWT access token request results in response status Unauthorized") {
            mvc.perform(
                get(JWT_ACCESS_TOKEN_PATH)
            )
                .andExpect(status().isUnauthorized)
        }

        test("token exchange request results in SAML token response") {
            every { ingressTokenValidator.validate(any()) } returns mockk(relaxed = true)
            every { serviceClient.doPost(any(), any(), any(), any()) } returns SAML_TOKEN_RESPONSE_BODY

            mvc.perform(
                post(TOKEN_EXCHANGE_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                    .content(SAML_TOKEN_REQUEST_BODY)
            )
                .andExpect(status().isOk)
                .andExpect(content().json(SAML_TOKEN_RESPONSE_BODY))
        }

        test("token exchange request with service-user-ID results in SAML token response") {
            every { ingressTokenValidator.validate(any()) } returns mockk(relaxed = true)
            every { serviceClient.doPost(any(), any(), any(), any()) } returns SAML_TOKEN_RESPONSE_BODY

            mvc.perform(
                post("$TOKEN_EXCHANGE_PATH?serviceUserId=3")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                    .content(SAML_TOKEN_REQUEST_BODY)
            )
                .andExpect(status().isOk)
                .andExpect(content().json(SAML_TOKEN_RESPONSE_BODY))
        }

        test("unauthorized token exchange request results in response status Unauthorized") {
            mvc.perform(
                post(TOKEN_EXCHANGE_PATH)
                    .content(SAML_TOKEN_REQUEST_BODY)
            )
                .andExpect(status().isUnauthorized)
        }
    }
}

private const val BASE_PATH = "/rest/v1/sts/token"
private const val JWT_ACCESS_TOKEN_PATH = "$BASE_PATH?grant_type=client_credentials&scope=openid"
private const val TOKEN_EXCHANGE_PATH = "$BASE_PATH/exchange"

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
