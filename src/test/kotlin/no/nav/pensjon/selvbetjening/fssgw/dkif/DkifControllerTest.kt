package no.nav.pensjon.selvbetjening.fssgw.dkif

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.anyObject
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
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

@WebMvcTest(DkifController::class)
internal class DkifControllerTest {

    private val url = "/api/v1/personer/kontaktinformasjon?inkluderSikkerDigitalPost=false"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `DKIF request results in JSON response`() {
        val expectedIdent = "00000000000"
        val expectedConsumerId = "cid"
        Mockito.`when`(serviceClient.callService(anyObject(), anyObject())).thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders
                .get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personidenter", expectedIdent)
                .header("Nav-Consumer-Id", expectedConsumerId)
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }

    @Test
    fun `DKIF request should use srvpselv as default consumer ID`() {
        val expectedIdent = "00000000000"
        Mockito.`when`(serviceClient.callService(anyObject(), anyObject())).thenReturn("""{ "response": "bar"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders
                .get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personidenter", expectedIdent)
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }
}
