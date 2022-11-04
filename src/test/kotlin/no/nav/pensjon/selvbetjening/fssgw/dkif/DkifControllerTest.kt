package no.nav.pensjon.selvbetjening.fssgw.dkif

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `DKIF request results in JSON response`() {
        val expectedIdent = "00000000000"
        val expectedConsumerId = "cid"
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "bar"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders
                .get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personidenter", expectedIdent)
                .header("Nav-Consumer-Id", expectedConsumerId)
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{ "response": "bar"}"""))
    }

    @Test
    fun `DKIF request should use srvpselv as default consumer ID`() {
        val expectedIdent = "00000000000"
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn("""{ "response": "bar"}""")
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders
                .get(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header("Nav-Personidenter", expectedIdent)
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{ "response": "bar"}"""))
    }
}
