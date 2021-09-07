package no.nav.pensjon.selvbetjening.fssgw.dkif

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

@WebMvcTest(DkifController::class)
internal class DkifControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var dkifConsumer: DkifConsumer

    @Test
    fun `Dkif request results in JSON response`() {
        val expectedIdent = "00000000000"
        val expectedConsumerId = "cid"
        Mockito.`when`(dkifConsumer.getKontaktinfo(null, expectedConsumerId, false, expectedIdent)).thenReturn("""{ "response": "bar"}""")

        mvc.perform(MockMvcRequestBuilders
                .get("/api/dkif/api/v1/personer/kontaktinformasjon?inkluderSikkerDigitalPost=false")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personidenter", expectedIdent)
                .header("Nav-Consumer-Id", expectedConsumerId)
                .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }

    @Test
    fun `Dkif request should use srvpselv as default consumer id`() {
        val expectedIdent = "00000000000"
        Mockito.`when`(dkifConsumer.getKontaktinfo(null, "srvpselv", false, expectedIdent)).thenReturn("""{ "response": "bar"}""")

        mvc.perform(MockMvcRequestBuilders
                .get("/api/dkif/api/v1/personer/kontaktinformasjon?inkluderSikkerDigitalPost=false")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personidenter", expectedIdent)
                .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }
}