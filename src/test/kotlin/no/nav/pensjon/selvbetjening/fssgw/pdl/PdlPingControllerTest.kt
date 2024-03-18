package no.nav.pensjon.selvbetjening.fssgw.pdl

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PdlPingController::class)
internal class PdlPingControllerTest {

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
    fun `when OK then PDL ping request results in JSON response`() {
        `when`(serviceClient.doOptions(anyString(), anyMap())).thenReturn("""{ "response": "bar"}""")
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            options("/graphql")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{ "response": "bar"}"""))
    }

    @Test
    fun `when error then Spring API ping request responds with bad gateway and error message`() {
        `when`(
            serviceClient.doOptions(
                anyString(),
                anyMap())).thenAnswer { throw EgressException("""{"error": "oops"}""") }
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            options("/graphql")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(""))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
