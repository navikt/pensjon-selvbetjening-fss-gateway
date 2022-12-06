package no.nav.pensjon.selvbetjening.fssgw.fullmakt

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(PensjonFullmaktController::class)
internal class PensjonFullmaktControllerTest {

    private val auth = "Bearer jwt"

    @Autowired
    lateinit var mvc: MockMvc

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
        `when`(serviceClient.doGet(anyString(), anyMap()))
            .thenReturn(responseBody())
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(MockUtil.serviceTokenData())

        mvc.perform(
            get("/bprof/finnFullmakter")
                .header(HttpHeaders.AUTHORIZATION, auth)
                .header("aktorNr", "05845997316"))
            .andExpect(status().isOk)
            .andExpect(
                content().json(responseBody()))
    }

    companion object {
        private fun responseBody() = """
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
