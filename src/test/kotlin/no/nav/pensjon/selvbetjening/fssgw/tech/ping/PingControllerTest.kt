package no.nav.pensjon.selvbetjening.fssgw.tech.ping

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

    private val path = "/api/internal/"

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `when OK then ping request responds with OK`() {
        mvc.perform(
            get(path + "ping"))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().json("""{ "reponse": "pong" }"""))
    }
}
