package no.nav.pensjon.selvbetjening.fssgw.tech.health

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(HealthController::class)
internal class HealthControllerTest {

    private val path = "/internal/"

    @Autowired
    private val mvc: MockMvc? = null

    @Test
    fun isAlive() {
        mvc!!.perform(get(path + "liveness"))
                .andExpect(status().isOk)
    }

    @Test
    fun isReady() {
        mvc!!.perform(get(path + "readiness"))
                .andExpect(status().isOk)
    }
}
