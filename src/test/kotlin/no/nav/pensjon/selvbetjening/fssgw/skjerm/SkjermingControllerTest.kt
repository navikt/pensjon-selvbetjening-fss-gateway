package no.nav.pensjon.selvbetjening.fssgw.skjerm

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(SkjermingController::class)
internal class SkjermingControllerTest {

    @Autowired
    lateinit var mvc: MockMvc
    @MockBean
    lateinit var jwsValidator: JwsValidator
    @MockBean
    lateinit var consumer: SkjermingConsumer
    @Mock
    lateinit var claims: Claims

    @Test
    fun isSkjermet_returnsData_when_validToken() {
        Mockito.`when`(consumer.isSkjermet(anyObject(), anyObject())).thenReturn("false")
        Mockito.`when`(jwsValidator.validate("jwt")).thenReturn(claims)

        mvc.perform(
            MockMvcRequestBuilders.get("/skjermet?personident=01023456789")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("false"))
    }

    @Test
    fun isSkjermet_returnsStatusUnauthorized_when_invalidToken() {
        Mockito.`when`(jwsValidator.validate("jwt")).thenThrow(JwtException("bad token"))

        mvc.perform(
            MockMvcRequestBuilders.get("/skjermet?personident=01023456789")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.content().string("Unauthorized"))

    }

    /**
     * Hack to make Mockito argument matchers work with Kotlin
     */
    private fun <T> anyObject(): T {
        Mockito.anyObject<T>()
        return null as T
    }
}
