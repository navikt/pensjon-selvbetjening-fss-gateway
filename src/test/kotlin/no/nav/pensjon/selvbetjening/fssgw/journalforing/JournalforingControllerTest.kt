package no.nav.pensjon.selvbetjening.fssgw.journalforing

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

@WebMvcTest(JournalforingController::class)
internal class JournalforingControllerTest{
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var journalforingConsumer: JournalforingConsumer

    @Test
    fun `Journalforing request results in JSON response`() {
        Mockito.`when`(journalforingConsumer.opprettJournalpost("foo", null, true)).thenReturn("""{ "response": "bar"}""")

        mvc.perform(MockMvcRequestBuilders.post("/api/journalforing?forsoekFerdigstill=true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }
}