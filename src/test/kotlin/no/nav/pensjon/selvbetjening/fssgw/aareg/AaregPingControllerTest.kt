package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.common.ConsumerException
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(AaregPingController::class)
internal class AaregPingControllerTest{
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var serviceClient: ServiceClient
    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter
    @MockBean
    lateinit var authValidator: JwsValidator

    @Test
    fun `when OK then Aareg API ping request responds with OK`() {
        Mockito.`when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap())).thenReturn("Ok")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
                MockMvcRequestBuilders.get("/aareg-services/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().string("Ok"))
    }

    @Test
    fun `when error then Spring API ping request responds with bad gateway and error message`() {
        Mockito.`when`(serviceClient.doGet(ArgumentMatchers.anyString(), ArgumentMatchers.anyMap()))
                .thenAnswer { throw ConsumerException("""{"error": "oops"}""") }
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
                MockMvcRequestBuilders.get("/aareg-services/api/ping")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .content(""))
                .andExpect(MockMvcResultMatchers.status().isBadGateway)
                .andExpect(MockMvcResultMatchers.content().json("""{"error": "oops"}"""))
    }
}