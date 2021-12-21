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

    @Test
    fun `when OK then sakssammendrag request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/springapi/sak/sammendrag")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("fnr", "01023456789")
                .header("Nav-Call-Id", "nav-call-id"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "bar"}"""))
    }

    @Test
    fun `when OK then ping request responds with OK`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject())).thenReturn("Ok")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/springapi/ping")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content(""))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
