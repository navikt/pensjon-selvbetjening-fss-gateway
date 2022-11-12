package no.nav.pensjon.selvbetjening.fssgw.tech.health

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PingController::class)
internal class PingControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `ping request responds with status OK and content pong`() {
        mvc.perform(
            get("/internal/ping"))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE))
            .andExpect(content().string("pong"))
    }
}
