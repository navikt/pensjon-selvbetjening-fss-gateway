package no.nav.pensjon.selvbetjening.fssgw.pen

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PenController::class)
internal class PenControllerTest {

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
    fun `when OK then AFP-historikk request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "AFP-historikken"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/person/afphistorikk")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "AFP-historikken"}"""))
    }

    @Test
    fun `when OK then uforehistorikk request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "uførehistorikken"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/person/uforehistorikk")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uførehistorikken"}"""))
    }

    @Test
    fun `when OK then uttaksgrad person request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "uttaksgraden"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/uttaksgrad/person?sakType=ALDER")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uttaksgraden"}"""))
    }

    @Test
    fun `when OK then uttaksgrad search request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "uttaksgradene"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/uttaksgrad/search?vedtakId=1&vedtakId=2&vedtakId=3")
                .header(HttpHeaders.AUTHORIZATION, auth))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uttaksgradene"}"""))
    }

    @Test
    fun `when OK then krav request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "kravet"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

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
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn("""{ "response": "sammendraget"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

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
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

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
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/springapi/vedtak/bestemgjeldende")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("fnr", "01023456789")
                .header("fom", "2021-02-03")
                .header("Nav-Call-Id", "ID 1"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "vedtakene"}"""))
    }
}
