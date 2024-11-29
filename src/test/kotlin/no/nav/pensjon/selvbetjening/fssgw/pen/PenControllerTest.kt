package no.nav.pensjon.selvbetjening.fssgw.pen

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PenController::class)
internal class PenControllerTest {

    private val auth = "Bearer jwt"

    @Autowired
    lateinit var mvc: MockMvc

    @Mock
    lateinit var claims: Claims

    @MockitoBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockitoBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockitoBean
    lateinit var serviceClient: ServiceClient

    @MockitoBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then AFP-historikk request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "AFP-historikken"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/api/person/afphistorikk")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "AFP-historikken"}"""))
    }

    @Test
    fun `when OK then uforehistorikk request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "uførehistorikken"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/api/person/uforehistorikk")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uførehistorikken"}"""))
    }

    @Test
    fun `when OK then uttaksgrad person request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "uttaksgraden"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/api/uttaksgrad/person?sakType=ALDER")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uttaksgraden"}"""))
    }

    @Test
    fun `when OK then uttaksgrad search request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "uttaksgradene"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/api/uttaksgrad/search?vedtakId=1&vedtakId=2&vedtakId=3")
                .header(HttpHeaders.AUTHORIZATION, auth))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uttaksgradene"}"""))
    }

    @Test
    fun `when OK then krav request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "kravet"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/springapi/krav")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("fnr", "01023456789")
                .header("Nav-Call-Id", "ID 1"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "kravet"}"""))
    }

    @Test
    fun `when OK then sakssammendrag request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "sammendraget"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/springapi/sak/sammendrag")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("fnr", "01023456789")
                .header("Nav-Call-Id", "ID 1"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "sammendraget"}"""))
    }

    @Test
    fun `when OK then vedtak request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "vedtakene"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/springapi/vedtak?sakstype=typen&alleVedtak=true&fom=2021-02-03")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("fnr", "01023456789")
                .header("Nav-Call-Id", "ID 1"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "vedtakene"}"""))
    }

    @Test
    fun `when OK then bestem gjeldende vedtak request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "vedtakene"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/pen/springapi/vedtak/bestemgjeldende")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("fnr", "01023456789")
                .header("fom", "2021-02-03")
                .header("Nav-Call-Id", "ID 1"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "vedtakene"}"""))
    }

    @Test
    fun `when 4xx error then PEN request responds with 4xx and error message`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString(), anyBoolean()))
            .thenAnswer { throw EgressException("""{"error": "oops"}""", HttpStatus.CONFLICT) }
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            post("/pen/springapi/simulering/alderspensjon")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content("body"))
            .andExpect(status().isConflict)
            .andExpect(content().json("""{"error": "oops"}"""))
    }

    @Test
    fun `when 5xx error then PEN request responds with 502 and error message`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString(), anyBoolean()))
            .thenAnswer { throw EgressException("""{"error": "oops"}""", HttpStatus.INTERNAL_SERVER_ERROR) }
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            post("/pen/springapi/uttaksalder")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content("body"))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
