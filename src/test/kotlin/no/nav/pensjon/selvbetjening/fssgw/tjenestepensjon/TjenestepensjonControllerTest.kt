package no.nav.pensjon.selvbetjening.fssgw.tjenestepensjon

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(TjenestepensjonController::class)
class TjenestepensjonControllerTest : FunSpec() {

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

        test("when OK then haveYtelse request returns data") {
            val egressToken = serviceTokenData()
            every { ingressTokenValidator.validate(any()) } returns mockk(relaxed = true)
            every { serviceClient.doGet(any(), any()) } returns """{ "value": true}"""
            every { egressTokenGetter.getServiceUserToken(serviceUserId = 1) } returns egressToken

            mvc.perform(
                get(PATH)
                    .header(HttpHeaders.AUTHORIZATION, INGRESS_AUTH)
                    .header("pid", PID)
            )
                .andExpect(status().isOk)
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json("""{ "value": true}"""))

            verify(exactly = 1) {
                serviceClient.doGet(
                    URL,
                    mapOf(
                        HttpHeaders.AUTHORIZATION to "Bearer ${egressToken.accessToken}",
                        "pid" to PID,
                        "Nav-Call-Id" to CALL_ID
                    )
                )
            }
        }

        test("when no Authorization in request then response is Unauthorized") {
            mvc.perform(
                get(PATH)
                    .header("pid", PID)
            )
                .andExpect(status().isUnauthorized)
                .andExpect(content().string("Unauthorized"))

            verify { serviceClient wasNot called }
        }
    }
}

private const val PID = "01023456789"
private const val CALL_ID = "call ID 1"
private const val INGRESS_AUTH = "Bearer JWT1"
private const val PATH = "/api/tjenestepensjon/haveYtelse"
private const val URL = "https://tp-q2.dev.intern.nav.no$PATH"
