package no.nav.pensjon.selvbetjening.fssgw.partner

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten.MaskinportenToken
import no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten.MaskinportenTokenGetter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(SimulerSPKTjenestepensjonController::class)
class SimulerSPKTjenestepensjonControllerTest : FunSpec() {

    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockkBean
    lateinit var serviceClient: ServiceClient

    @MockkBean
    lateinit var callIdGenerator: CallIdGenerator

    @MockkBean
    lateinit var egressTokenGetter: MaskinportenTokenGetter

    init {
        beforeSpec {
            every { callIdGenerator.newCallId() } returns "call ID 1"
        }

        test("when authenticated request then response is OK") {
            every { ingressTokenValidator.validate(any()) } returns mockk(relaxed = true)
            every { serviceClient.doPost(any(), any(), any(), any()) } returns RESPONSE_BODY
            every { egressTokenGetter.getToken(SCOPE) } returns MaskinportenToken(
                access_token = "jwt",
                token_type = "Bearer",
                expires_in = 1,
                scope = SCOPE
            )
            val expectedMediaType = MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString()

            mvc.perform(
                post(PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                    .header("scope", SCOPE)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .content(REQUEST_BODY)
            )
                .andExpect(status().isOk)
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(RESPONSE_BODY))

            verify(exactly = 1) {
                serviceClient.doPost(
                    uri = "$BASE_URL$PATH",
                    headers = mapOf(
                        "Content-Type" to expectedMediaType,
                        "Nav-Call-Id" to "call ID 1",
                        HttpHeaders.AUTHORIZATION to "Bearer jwt"
                    ),
                    body = REQUEST_BODY,
                    externalCall = true
                )
            }
        }

        test("unauthorized simulation request results in response status Unauthorized") {
            mvc.perform(
                post(PATH)
                    .header("scope", SCOPE)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .content(REQUEST_BODY)
            )
                .andExpect(status().isUnauthorized)
                .andExpect(content().string("Unauthorized"))

            verify { serviceClient wasNot called }
        }
    }
}

private const val BASE_URL = "https://api.preprod.spk.no"
private const val PATH = "/medlem/pensjon/prognose/v1"
private const val SCOPE = "test:spk:nav"
private const val REQUEST_BODY = """{"request":"body"}"""
private const val RESPONSE_BODY = """{"response":"body"}"""
