package no.nav.pensjon.selvbetjening.fssgw.esb.unt

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UsernameTokenController::class)
internal class UsernameTokenControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @Test
    fun `UsernameToken request results in OK token response`() {
        mvc.perform(
            get(PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
            .andExpect(status().isOk)
            .andExpect(content().string(RESPONSE_BODY))
    }

    @Test
    fun `unauthorized UsernameToken request results in response status Unauthorized`() {
        mvc.perform(
            get(PATH))
            .andExpect(status().isUnauthorized)
    }

    private companion object {
        private const val PATH = "/ws-support/unt"

        private const val RESPONSE_BODY =
            """<wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
                <wsse:UsernameToken>
                    <wsse:Username>srvpselv</wsse:Username>
                    <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">&amp;secret</wsse:Password>
                </wsse:UsernameToken>
            </wsse:Security>"""
    }
}
