package no.nav.pensjon.selvbetjening.fssgw.ereg

import no.nav.pensjon.selvbetjening.fssgw.common.ConsumerException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.pen.PenPingController
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(EregRestPingController::class)
internal class EregRestPingControllerTest{
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `when OK then Ereg API ping request responds with OK`() {
        Mockito.`when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap())).thenReturn("Ok")

        mvc.perform(
                MockMvcRequestBuilders.get("/ereg/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("Ok"))
    }

    @Test
    fun `when error then Ereg API ping request responds with bad gateway and error message`() {
        Mockito.`when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
                .thenAnswer { throw ConsumerException("""{"error": "oops"}""") }

        mvc.perform(
                MockMvcRequestBuilders.get("/ereg/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(""))
                .andExpect(MockMvcResultMatchers.status().isBadGateway)
                .andExpect(MockMvcResultMatchers.content().json("""{"error": "oops"}"""))
    }
}