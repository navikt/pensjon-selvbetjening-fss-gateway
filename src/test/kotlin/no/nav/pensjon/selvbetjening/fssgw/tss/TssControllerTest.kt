package no.nav.pensjon.selvbetjening.fssgw.tss

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.serviceTokenData
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
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(TssController::class)
internal class TssControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var egressTokenGetter: ServiceTokenGetter

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `hentSamhandler request results in samhandler response XML`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString())).thenReturn(RESPONSE_BODY)
        `when`(egressTokenGetter.getServiceUserToken()).thenReturn(serviceTokenData())
        val expectedMediaType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8)

        mvc.perform(
            post(HENT_SAMHANDLER_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content(REQUEST_BODY))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMediaType.toString()))
            .andExpect(content().xml(RESPONSE_BODY))
    }

    private companion object {
        const val HENT_SAMHANDLER_PATH = "/services/tss/hentSamhandler"

        const val REQUEST_BODY: String = """<?xml version="1.0" ?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header xmlns="http://www.w3.org/2005/08/addressing">
        <To>https://wasapp-q2.adeo.no/services/tss/hentSamhandler</To>
        <Action>http://nav.no/virksomhet/tjenester/samhandler/v2/Samhandler/hentSamhandlerNavnRequest</Action>
        <ReplyTo>
            <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>
        </ReplyTo>
        <FaultTo>
            <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>
        </FaultTo>
        <MessageID>uuid:fa435c19-5d71-4c78-9cd5-e261dc8c4e6b</MessageID>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
            <wsse:UsernameToken>
                <wsse:Username>P*****S</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">********</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soapenv:Header>
    <soapenv:Body>
        <sam:hentSamhandlerNavn xmlns:sam="http://nav.no/virksomhet/tjenester/samhandler/v2">
            <request>
                <tssEksternId>80000123456</tssEksternId>
            </request>
        </sam:hentSamhandlerNavn>
    </soapenv:Body>
</soapenv:Envelope>"""

        // Based on actual response from TSS in Q2
        const val RESPONSE_BODY: String =
            """<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
    <SOAP-ENV:Body>
        <hentSamhandlerNavnResponse xmlns="http://nav.no/virksomhet/tjenester/samhandler/v2">
            <response xmlns="">
                <samhandler>
                    <navn>Kommunal Landspensjonskasse             </navn>
                    <samhandlerIdent>
                        <ident>1234       </ident>
                        <identKode>
                            <kode>TPNR</kode>
                        </identKode>
                    </samhandlerIdent>
                    <samhandlerKode>
                        <kode>TEPE</kode>
                        <dekode>Tjenestepensjons leverandør   </dekode>
                    </samhandlerKode>
                    <gjelder>
                        <gjelderFom>2008-12-01</gjelderFom>
                    </gjelder>
                    <sprak>
                        <kode>NB  </kode>
                    </sprak>
                    <status>
                        <kode>GYLD</kode>
                        <dekode>Gyldig                    </dekode>
                    </status>
                    <utbetalingssperre>false</utbetalingssperre>
                    <sporing>
                        <dato>2015-06-27</dato>
                        <ident>AJOURHLD</ident>
                        <kildeNavn>ENH </kildeNavn>
                    </sporing>
                </samhandler>
            </response>
        </hentSamhandlerNavnResponse>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope>"""
    }
}
