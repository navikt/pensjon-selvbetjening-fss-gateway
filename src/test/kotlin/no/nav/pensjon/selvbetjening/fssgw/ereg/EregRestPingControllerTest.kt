package no.nav.pensjon.selvbetjening.fssgw.ereg

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(EregRestPingController::class)
internal class EregRestPingControllerTest{

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then Ereg API ping request responds with OK`() {
        `when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap())).thenReturn("Ok")

        mvc.perform(
                MockMvcRequestBuilders.get("/ereg/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content("foo"))
                .andExpect(status().isOk)
                .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then Ereg API ping request responds with bad gateway and error message`() {
        `when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
                .thenAnswer { throw EgressException("""{"error": "oops"}""") }

        mvc.perform(
                MockMvcRequestBuilders.get("/ereg/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(""))
                .andExpect(status().isBadGateway)
                .andExpect(content().json("""{"error": "oops"}"""))
    }
}
