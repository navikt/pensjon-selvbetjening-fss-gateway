package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.common.ConsumerException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PenPingController::class)
internal class PenPingControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var authValidator: BasicAuthValidator

    @MockBean
    lateinit var serviceClient: ServiceClient

    private val credentials = "cred"

    @Test
    fun `when OK then ping request responds with OK`() {
        `when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject())).thenReturn("Ok")
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            get("/pen/springapi/ping")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then ping request responds with bad gateway and error message`() {
        `when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenAnswer { throw ConsumerException("oops") }
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            get("/pen/springapi/ping")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content(""))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
