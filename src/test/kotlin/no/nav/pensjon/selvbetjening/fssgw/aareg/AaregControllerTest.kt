package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
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

@WebMvcTest(AaregController::class)
internal class AaregControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var consumer: ServiceClient

    @Test
    fun `Aareg request results in JSON response`() {
        val expectedIdent = "00000000000"
        Mockito.`when`(consumer.callService(anyObject(), anyObject())).thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(ServiceTokenData("j.w.t","JWT", LocalDateTime.now(), 60L))

        mvc.perform(
            MockMvcRequestBuilders
                .get("/aareg-services/api/v1/arbeidstaker/arbeidsforhold")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personident", expectedIdent)
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }

    /**
     * Hack to make Mockito argument matchers work with Kotlin
     */
    private fun <T> anyObject(): T {
        Mockito.anyObject<T>()
        return null as T
    }
}
