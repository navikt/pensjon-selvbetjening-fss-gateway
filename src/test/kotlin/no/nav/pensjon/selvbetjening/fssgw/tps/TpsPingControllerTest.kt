package no.nav.pensjon.selvbetjening.fssgw.tps

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TpsPingController::class)
internal class TpsPingControllerTest {

    private val path = "/tpsws-aura/ws/Person/v3"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var authValidator: BasicAuthValidator

    @MockBean
    lateinit var serviceClient: ServiceClient

    private val credentials = "cred"

    @Test
    fun `when OK then person request responds with OK`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString())).thenReturn("Ok")
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            post(path)
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then person request responds with bad gateway and error message`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString()))
            .thenAnswer { throw ConsumerException("oops") }
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            post(path)
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content("foo"))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
