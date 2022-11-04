package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ConsumerException
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

@WebMvcTest(AaregPingController::class)
internal class AaregPingControllerTest{

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then Aareg API ping request responds with OK`() {
        `when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap())).thenReturn("Ok")

        mvc.perform(
                MockMvcRequestBuilders.get("/aareg-services/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content("foo"))
                .andExpect(status().isOk)
                .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then Aareg API ping request responds with bad gateway and error message`() {
        `when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
                .thenAnswer { throw ConsumerException("""{"error": "oops"}""") }

        mvc.perform(
                MockMvcRequestBuilders.get("/aareg-services/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(""))
                .andExpect(status().isBadGateway)
                .andExpect(content().json("""{"error": "oops"}"""))
    }
}
