package no.nav.pensjon.selvbetjening.fssgw.sts

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ConsumerException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StsPingController::class)
internal class StsPingControllerTest {

    private val path = "/rest/v1/sts"
    private val credentials = "cred"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var authValidator: BasicAuthValidator

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then JWT token request responds with OK`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("Ok")
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            get("$path/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().string("Ok"))
    }

    @Test
    fun `when OK then SAML token request responds with OK`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("Ok")
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            get("$path/samltoken")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then JWT token request responds with bad gateway and error message`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenAnswer { throw ConsumerException("""{"error": "oops"}""") }
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            get("$path/token")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content(""))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
