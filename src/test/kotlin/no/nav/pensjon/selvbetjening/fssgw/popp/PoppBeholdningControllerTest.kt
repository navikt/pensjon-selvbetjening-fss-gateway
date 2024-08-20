package no.nav.pensjon.selvbetjening.fssgw.popp

import io.jsonwebtoken.Claims
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PoppBeholdningController::class)
internal class PoppBeholdningControllerTest {

    private val poppApiUrl = "/popp/api/"
    private val auth = "Bearer jwt"

    @Autowired
    lateinit var mvc: MockMvc

    @Mock
    lateinit var claims: Claims

    @MockBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `when OK then beholdning request returns data`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString())).thenReturn("""{ "response": "beholdningen"}""")
        `when`(egressTokenGetter.getServiceUserToken(serviceUserId = 1)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

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
            .andExpect(status().isOk)
            .andExpect(content().json("""{"response": "beholdningen"}"""))
    }
}
