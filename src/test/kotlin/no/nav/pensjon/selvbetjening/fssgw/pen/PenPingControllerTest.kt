package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.EgressException
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

@WebMvcTest(PenPingController::class)
internal class PenPingControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var authValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then Spring API ping request responds with OK`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("Ok")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/springapi/ping")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().string("Ok"))
    }

    @Test
    fun `when error then Spring API ping request responds with bad gateway and error message`() {
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenAnswer { throw EgressException("""{"error": "oops"}""") }
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/pen/springapi/ping")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .content(""))
            .andExpect(status().isBadGateway)
            .andExpect(content().json("""{"error": "oops"}"""))
    }
}
