package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.anyObject
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
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

@WebMvcTest(EsbController::class)
internal class EsbControllerTest {

    private val fullmaktPath = "/nav-cons-pen-pselv-fullmaktWeb/sca/PSELVFullmaktWSEXP"
    private val personPath = "/nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP"
    private val pingPath = "/nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `fullmakt request results in fullmakt response XML`() {
        Mockito.`when`(serviceClient.doPost(anyObject(), anyObject(), anyObject()))
            .thenReturn(EsbXml.fullmaktResponseBody)
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.post(fullmaktPath)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().xml(EsbXml.fullmaktResponseBody))
    }

    @Test
    fun `person request results in person response XML`() {
        Mockito.`when`(serviceClient.doPost(anyObject(), anyObject(), anyObject()))
            .thenReturn(EsbXml.personResponseBody)
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.post(personPath)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().xml(EsbXml.personResponseBody))
    }

    @Test
    fun `ping request results in ping response XML`() {
        Mockito.`when`(serviceClient.doPost(anyObject(), anyObject(), anyObject()))
            .thenReturn(EsbXml.pingResponseBody)
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.post(pingPath)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().xml(EsbXml.pingResponseBody))
    }
}
