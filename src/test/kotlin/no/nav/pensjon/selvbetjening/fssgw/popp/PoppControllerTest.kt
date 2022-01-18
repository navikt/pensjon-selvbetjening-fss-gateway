package no.nav.pensjon.selvbetjening.fssgw.popp

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(PoppController::class)
internal class PoppControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    private val poppApiUrl = "/popp/api/"
    private val auth = "Bearer jwt"

    @Test
    fun `when OK then opptjeningsgrunnlag request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "opptjeningsgrunnlaget"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.get(poppApiUrl + "opptjeningsgrunnlag/01023456789?fomAr=2001&tomAr=2022")
                .header(HttpHeaders.AUTHORIZATION, auth))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("""{"response": "opptjeningsgrunnlaget"}"""))
    }

    @Test
    fun `when OK then pensjonspoeng request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "pensjonspoengene"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.get(poppApiUrl + "pensjonspoeng/01023456789")
                .header(HttpHeaders.AUTHORIZATION, auth))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("""{"response": "pensjonspoengene"}"""))
    }

    @Test
    fun `when OK then restpensjon request returns data`() {
        Mockito.`when`(serviceClient.doGet(MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "restpensjonen"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.get(poppApiUrl + "restpensjon/01023456789?hentSiste=false")
                .header(HttpHeaders.AUTHORIZATION, auth))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("""{"response": "restpensjonen"}"""))
    }

    @Test
    fun `when OK then beholdning request returns data`() {
        Mockito.`when`(serviceClient.doPost(MockUtil.anyObject(), MockUtil.anyObject(), MockUtil.anyObject()))
            .thenReturn("""{ "response": "beholdningen"}""")
        Mockito.`when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            MockMvcRequestBuilders.post(poppApiUrl + "beholdning")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .content(
                    """
                   {
                     "fnr": "01023456789",
                     "beholdningType":"PEN_B",
                     "serviceDirectiveTPOPP006", "INKL_GRUNNLAG"
                    }"""))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().json("""{"response": "beholdningen"}"""))
    }
}
