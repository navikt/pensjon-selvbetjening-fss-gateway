package no.nav.pensjon.selvbetjening.fssgw.pen

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PenController::class)
internal class PenControllerTest {

    @Autowired
    lateinit var mvc: MockMvc
    @MockBean
    lateinit var jwsValidator: JwsValidator
    @MockBean
    lateinit var bodilessPenConsumer: BodilessPenConsumer
    @MockBean
    lateinit var penConsumer: PenConsumer
    @Mock
    lateinit var claims: Claims

    @Test
    fun `when OK then sakssammendrag request returns data`() {
        val callId = "nav-call-id"
        Mockito.`when`(bodilessPenConsumer.callPen("/pen/springapi/sak/sammendrag", callId, "01023456789", HttpMethod.GET)).thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(jwsValidator.validate("jwt")).thenReturn(claims)

        mvc.perform(
            get("/pen/springapi/sak/sammendrag")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("fnr", "01023456789")
                .header("Nav-Call-Id", callId))
            .andExpect(status().isOk)
            .andExpect(content().json("{'response':'bar'}"))
    }

    @Test
    fun `when OK then ping request responds with OK`() {
        Mockito.`when`(bodilessPenConsumer.ping("/pen/springapi/ping")).thenReturn("Ok")

        mvc.perform(get("/pen/springapi/ping")
                .content("foo"))
                .andExpect(status().isOk)
                .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then ping request responds with bad gateway and error message`() {
        Mockito.`when`(bodilessPenConsumer.ping("/pen/springapi/ping")).thenAnswer { throw PenException("oops") }

        mvc.perform(get("/pen/springapi/ping")
                .content(""))
                .andExpect(status().isBadGateway)
                .andExpect(content().json("{'error': 'oops'}"))
    }
}
