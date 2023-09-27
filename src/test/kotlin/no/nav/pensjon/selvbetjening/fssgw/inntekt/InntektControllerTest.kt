package no.nav.pensjon.selvbetjening.fssgw.inntekt

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(InntektController::class)
internal class InntektControllerTest {

    private val auth = "Bearer jwt"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then ForventedeInntekter request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "ForventedeInntekter"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/inntektskomponenten-ws/rs/api/v1/forventetinntekt")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789")
                .header("aar-list", "2020")
                .header("aar-list", "2021"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "ForventedeInntekter"}"""))
    }

    @Test
    fun `when OK then ForventedeInntekter post returns data`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString()))
            .thenReturn("""{ "response": "ForventedeInntekter"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            post("/inntektskomponenten-ws/rs/api/v1/forventetinntekt")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .content("body"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "ForventedeInntekter"}"""))
    }

    @Test
    fun `when OK then hentdetaljerteabonnerteinntekter post returns data`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString()))
            .thenReturn("""{ "response": "hentdetaljerteabonnerteinntekter"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            post("/inntektskomponenten-ws/rs/api/v1/hentdetaljerteabonnerteinntekter")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .content("body"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "hentdetaljerteabonnerteinntekter"}"""))
    }

    @Test
    fun `when OK then hentabonnerteinntekterbolk post returns data`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString()))
            .thenReturn("""{ "response": "hentabonnerteinntekterbolk"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            post("/inntektskomponenten-ws/rs/api/v1/hentabonnerteinntekterbolk")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .content("body"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "hentabonnerteinntekterbolk"}"""))
    }
}
