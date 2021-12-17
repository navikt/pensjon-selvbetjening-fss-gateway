package no.nav.pensjon.selvbetjening.fssgw.skjerm

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.anyObject
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDateTime

@WebMvcTest(SkjermingController::class)
internal class SkjermingControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `isSkjermet returns data when valid token`() {
        Mockito.`when`(serviceClient.callService(anyObject(), anyObject())).thenReturn("false")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(ServiceTokenData("j.w.t","JWT", LocalDateTime.now(), 60L))

        mvc.perform(
            MockMvcRequestBuilders.get("/skjermet?personident=01023456789")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().string("false"))
    }

    @Test
    fun `isSkjermet returns status Unauthorized when invalid token`() {
        Mockito.`when`(jwsValidator.validate("jwt")).thenThrow(JwtException("bad token"))

        mvc.perform(
            MockMvcRequestBuilders.get("/skjermet?personident=01023456789")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.content().string("Unauthorized"))
    }
}
