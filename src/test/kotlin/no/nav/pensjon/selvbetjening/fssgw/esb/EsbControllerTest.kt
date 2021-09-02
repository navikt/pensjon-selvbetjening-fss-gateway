package no.nav.pensjon.selvbetjening.fssgw.esb

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

@WebMvcTest(EsbController::class)
internal class EsbControllerTest {

    private val fullmaktPath = "/nav-cons-pen-pselv-fullmaktWeb/sca/PSELVFullmaktWSEXP"
    private val personPath = "/nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP"
    private val pingPath = "/nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var esbConsumer: EsbConsumer

    @Test
    fun `fullmakt request results in fullmakt response XML`() {
        Mockito.`when`(esbConsumer.callEsb(fullmaktPath, "foo")).thenReturn(EsbXml.fullmaktResponseBody)

        mvc.perform(
            MockMvcRequestBuilders.post("/api/esb/fullmakt")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().xml(EsbXml.fullmaktResponseBody))
    }

    @Test
    fun `person request results in person response XML`() {
        Mockito.`when`(esbConsumer.callEsb(personPath, "foo")).thenReturn(EsbXml.personResponseBody)

        mvc.perform(
            MockMvcRequestBuilders.post("/api/esb/person")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().xml(EsbXml.personResponseBody))
    }

    @Test
    fun `ping request results in ping response XML`() {
        Mockito.`when`(esbConsumer.callEsb(pingPath, "foo")).thenReturn(EsbXml.pingResponseBody)

        mvc.perform(
            MockMvcRequestBuilders.post("/api/esb/ping")
                .content("foo")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().xml(EsbXml.pingResponseBody))
    }
}
