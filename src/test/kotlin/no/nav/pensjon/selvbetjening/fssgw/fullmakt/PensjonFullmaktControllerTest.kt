package no.nav.pensjon.selvbetjening.fssgw.fullmakt

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PensjonFullmaktController::class)
internal class PensjonFullmaktControllerTest {

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
    fun `when OK then finnFullmakter request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn(responseFinnFullmakterBody())
        `when`(egressTokenGetter.getServiceUserToken(useServiceUser2 = false)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/bprof/finnFullmakter")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("aktorNr", "05845997316"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(responseFinnFullmakterBody()))
    }

    @Test
    fun `when OK then harFullmaktsforhold request returns data`() {
        `when`(serviceClient.doGet(anyString(), anyMap())).thenReturn(responseHarFullmaktsforholdBody())
        `when`(egressTokenGetter.getServiceUserToken(useServiceUser2 = false)).thenReturn(serviceTokenData())
        `when`(ingressTokenValidator.validate(anyString())).thenReturn(claims)

        mvc.perform(
            get("/harFullmaktsforhold")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("fNrGiver", "05845997316")
                .header("fNrFullmektig", "00000000000"))

            .andExpect(status().isOk)
            .andExpect(
                content().string(responseHarFullmaktsforholdBody()))
    }

    companion object {
        private fun responseHarFullmaktsforholdBody() = """true"""

        private fun responseFinnFullmakterBody() = """
    {
        "aktor": {
            "kodeAktorType": "PERSON",
            "aktorNr": "05845997316",
            "fullmaktTil": [
                {
                    "sistBrukt": 1655236800000,
                    "kodeFullmaktType": "SELVBET",
                    "aktorMottar": {
                        "kodeAktorType": "PERSON",
                        "aktorNr": "05845997316",
                        "fullmaktTil": [],
                        "fullmaktFra": []
                    },
                    "aktorGir": {
                        "kodeAktorType": "PERSON",
                        "aktorNr": "01865499538",
                        "fullmaktTil": [],
                        "fullmaktFra": []
                    },
                    "opprettetDato": 1655245613272,
                    "kodeFullmaktNiva": "FULLSTENDIG",
                    "opprettetAv": "unknown-user",
                    "endretDato": 1655245613272,
                    "endretAv": "unknown-user",
                    "fomDato": 1655244000000,
                    "tomDato": null,
                    "gyldig": true,
                    "fullmaktId": 100214911,
                    "versjon": 9,
                    "fagomrade": "PEN"
                }
            ],
            "fullmaktFra": []
        }
    }                
                """
    }
}
