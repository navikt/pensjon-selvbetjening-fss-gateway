package no.nav.pensjon.selvbetjening.fssgw.tech.health

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(HealthController::class)
internal class HealthControllerTest {

    private val path = "/internal/"

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `liveness shall respond with status OK`() {
        mvc.perform(get(path + "liveness"))
                .andExpect(status().isOk)
    }

    @Test
    fun `readiness shall respond with status OK`() {
        mvc.perform(get(path + "readiness"))
                .andExpect(status().isOk)
    }
}
