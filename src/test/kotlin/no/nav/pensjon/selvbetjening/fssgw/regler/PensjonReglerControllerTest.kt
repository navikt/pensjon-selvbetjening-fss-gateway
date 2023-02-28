package no.nav.pensjon.selvbetjening.fssgw.regler

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PensjonReglerController::class)
internal class PensjonReglerControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `hentGrunnbelopListe request results in JSON response`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString()))
            .thenReturn(RESPONSE_BODY)

        mvc.perform(
            post(HENT_GRUNNBELOP_LISTE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(content().json(RESPONSE_BODY))
    }

    @Test
    fun `unauthorized hentGrunnbelopListe request results in response status Unauthorized`() {
        mvc.perform(
            post(HENT_GRUNNBELOP_LISTE_PATH)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isUnauthorized)
    }

    private companion object {
        const val HENT_GRUNNBELOP_LISTE_PATH = "/api/hentGrunnbelopListe"

        const val REQUEST_BODY = """{
    "fom": 1676042011910,
    "tom": 1676042011910
}"""

        const val RESPONSE_BODY = """{
    "satsResultater": ["java.util.ArrayList", [{
        "fom": 1651399200000,
        "tom": 253402254000000,
        "verdi": 111477.0
    }]]
}"""
    }
}
