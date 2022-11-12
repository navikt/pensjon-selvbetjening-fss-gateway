package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(EsbController::class)
internal class EsbControllerTest {

    private val penPersonPath = "/nav-cons-pen-pen-personWeb/sca/PENPersonWSEXP"
    private val ppen015Path = "/nav-cons-pen-pen-ppen015Web/sca/PENPPEN015WSEXP"
    private val brukerprofilPath = "/nav-cons-pen-pselv-brukerprofilWeb/sca/PSELVBrukerprofilWSEXP"
    private val fullmaktPath = "/nav-cons-pen-pselv-fullmaktWeb/sca/PSELVFullmaktWSEXP"
    private val henvendelsePath = "/nav-cons-pen-pselv-henvendelseWeb/sca/PSELVHenvendelseWSEXP"
    private val inntektPath = "/nav-cons-pen-pselv-inntektWeb/sca/PSELVInntektWSEXP"
    private val pselvPersonPath = "/nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP"
    private val ppen004Path = "/nav-cons-pen-pselv-ppen004Web/sca/PENPPEN004WSEXP"
    private val samhandlerPath = "/nav-cons-pen-pselv-samhandlerWeb/sca/PSELVSamhandlerWSEXP"
    private val tjenestepensjonPath = "/nav-cons-pen-pselv-tjenestepensjonWeb/sca/PSELVTjenestepensjonWSEXPP"
    private val utbetalingPath = "/nav-cons-pen-pselv-utbetalingWeb/sca/PSELVUtbetalingWSEXP"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `PEN person request results in PEN person response XML`() {
        doTest(penPersonPath, EsbXml.penPersonResponseBody)
    }

    @Test
    fun `PPEN015 request results in PPEN015 response XML`() {
        doTest(ppen015Path, EsbXml.ppen015ResponseBody)
    }

    @Test
    fun `brukerprofil request results in brukerprofil response XML`() {
        doTest(brukerprofilPath, EsbXml.brukerprofilResponseBody)
    }

    @Test
    fun `fullmakt request results in fullmakt response XML`() {
        doTest(fullmaktPath, EsbXml.fullmaktResponseBody)
    }

    @Test
    fun `henvendelse request results in henvendelse response XML`() {
        doTest(henvendelsePath, EsbXml.henvendelseResponseBody)
    }

    @Test
    fun `inntekt request results in inntekt response XML`() {
        doTest(inntektPath, EsbXml.inntektResponseBody)
    }

    @Test
    fun `person request results in person response XML`() {
        doTest(pselvPersonPath, EsbXml.pselvPersonResponseBody)
    }

    @Test
    fun `PPEN004 request results in PPEN004 response XML`() {
        doTest(ppen004Path, EsbXml.ppen004ResponseBody)
    }

    @Test
    fun `samhandler request results in samhandler response XML`() {
        doTest(samhandlerPath, EsbXml.samhandlerResponseBody)
    }

    @Test
    fun `tjenestepensjon request results in tjenestepensjon response XML`() {
        doTest(tjenestepensjonPath, EsbXml.tjenestepensjonResponseBody)
    }

    @Test
    fun `utbetaling request results in utbetaling response XML`() {
        doTest(utbetalingPath, EsbXml.utbetalingResponseBody)
    }

    private fun doTest(path: String, expectedResponseBody: String) {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString())).thenReturn(expectedResponseBody)
        `when`(callIdGenerator.newCallId()).thenReturn("call ID 1")
        val expectedMediaType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8)

        mvc.perform(
            post(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content("""<?xml version="1.0" encoding="UTF-8"?><auth>__password__</auth><foo/>"""))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMediaType.toString()))
            .andExpect(content().xml(expectedResponseBody))

        verify(serviceClient, times(1)).doPost(
            "https://tjenestebuss-q2.adeo.no$path",
            mapOf("Content-Type" to "text/xml;charset=UTF-8", "Nav-Call-Id" to "call ID 1"),
            """<?xml version="1.0" encoding="UTF-8"?><auth>secret</auth><foo/>""")
    }
}
