package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.common.ConsumerException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.Mockito
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

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    private val auth = "Bearer jwt"

    @Test
    fun `when OK then AFP-historikk request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "AFP-historikken"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/person/afphistorikk")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "AFP-historikken"}"""))
    }

    @Test
    fun `when OK then uforehistorikk request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "uførehistorikken"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/person/uforehistorikk")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uførehistorikken"}"""))
    }

    @Test
    fun `when OK then uttaksgrad person request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "uttaksgraden"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/uttaksgrad/person?sakType=ALDER")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("pid", "01023456789"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uttaksgraden"}"""))
    }

    @Test
    fun `when OK then uttaksgrad search request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "uttaksgradene"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/api/uttaksgrad/search?vedtakId=1&vedtakId=2&vedtakId=3")
                .header(HttpHeaders.AUTHORIZATION, auth))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "uttaksgradene"}"""))
    }

    @Test
    fun `when OK then krav request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "kravet"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

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
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "sammendraget"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

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
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "vedtakene"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

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
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "vedtakene"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

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
    fun `when OK then ping request responds with OK`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject())).thenReturn("Ok")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/springapi/ping")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then ping request responds with bad gateway and error message`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenAnswer { throw ConsumerException("oops") }
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/springapi/ping")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .content(""))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
