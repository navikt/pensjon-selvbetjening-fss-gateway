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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PenController::class)
internal class PenControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var penConsumer: PenConsumer

    @Mock
    lateinit var claims: Claims

    @Test
    fun penRequest() {
        Mockito.`when`(penConsumer.callPen("foo", null, "fnr")).thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(jwsValidator.validate("jwt")).thenReturn(claims)
        Mockito.`when`(claims["pid"]).thenReturn("fnr")

        mvc.perform(post("/api/pen")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
                .andExpect(status().isOk)
                .andExpect(content().json("{'response':'bar'}"))
    }
}
