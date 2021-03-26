package no.nav.pensjon.selvbetjening.fssgw.pdl

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PdlController::class)
internal class PdlControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var pdlConsumer: PdlConsumer

    @Test
    fun pdlRequest() {
        Mockito.`when`(pdlConsumer.callPdl("foo", null)).thenReturn("""{ "response": "bar"}""")

        mvc.perform(post("/api/pdl")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
                .andExpect(status().isOk)
                .andExpect(content().json("{'response':'bar'}"))
    }
}
