package no.nav.pensjon.selvbetjening.fssgw.tps

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test

import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(TpsController::class)
internal class TpsControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var tpsConsumer: TpsConsumer

    @Test
    fun `person request results in person response XML`() {
        Mockito.`when`(tpsConsumer.getPerson("foo")).thenReturn(TpsXml.personResponseBody)

        mvc.perform(MockMvcRequestBuilders.post("/api/tps/person")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().xml(TpsXml.personResponseBody))
    }

    @Test
    fun `ping request results in ping response XML`() {
        Mockito.`when`(tpsConsumer.ping("foo")).thenReturn(TpsXml.pingResponseBody)

        mvc.perform(MockMvcRequestBuilders.post("/api/tps/ping")
                .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().xml(TpsXml.pingResponseBody))
    }
}
