package no.nav.pensjon.selvbetjening.fssgw.partner

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(PepGatewayController::class)
class PepGatewayControllerTest : FunSpec() {

    @Autowired
    lateinit var mvc: MockMvc

    @MockkBean
    lateinit var ingressTokenValidator: JwsValidator

    @MockkBean
    lateinit var serviceClient: ServiceClient

    @MockkBean
    lateinit var callIdGenerator: CallIdGenerator

    init {
        beforeSpec {
            every { callIdGenerator.newCallId() } returns "call ID 1"
        }

        test("when authenticated request then response is OK") {
            every { ingressTokenValidator.validate(any()) } returns mockk(relaxed = true)
            every { serviceClient.doPost(any(), any(), any(), any()) } returns RESPONSE_BODY
            val expectedMediaType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8)

            mvc.perform(
                post(URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                    .content(REQUEST_BODY)
            )
                .andExpect(status().isOk)
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMediaType.toString()))
                .andExpect(content().xml(RESPONSE_BODY))

            verify(exactly = 1) {
                serviceClient.doPost(
                    uri = URL,
                    headers = mapOf("Content-Type" to "text/xml;charset=UTF-8", "Nav-Call-Id" to "call ID 1"),
                    body = REQUEST_BODY
                )
            }
        }

        test("when no Authorization in request then response is Unauthorized") {
            mvc.perform(
                post(URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                    .content(REQUEST_BODY)
            )
                .andExpect(status().isUnauthorized)
                .andExpect(content().string("Unauthorized"))

            verify { serviceClient wasNot called }
        }
    }
}

private const val URL =
    "https://pep-gw-q1.oera-q.local:9443/kalkulator.pensjonsrettighetstjeneste/v3/kalkulatorPensjonTjeneste"

private const val REQUEST_BODY =
    """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:typ="http://x.no/api/pensjonskalkulator/v3/typer">
    <soapenv:Header>
        <saml2:Assertion xmlns:saml2="urn:oasis:names:tc:SAML:2.0:assertion" ID="x" IssueInstant="2023-06-30T12:22:50.503Z" Version="2.0">
            <saml2:Issuer>x</saml2:Issuer>
            <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
                <SignedInfo>
                    <CanonicalizationMethod Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
                    <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#rsa-sha1"/>
                    <Reference URI="x">
                        <Transforms>
                            <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
                            <Transform Algorithm="http://www.w3.org/2001/10/xml-exc-c14n#"/>
                        </Transforms>
                        <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
                        <DigestValue>x</DigestValue>
                    </Reference>
                </SignedInfo>
                <SignatureValue>x</SignatureValue>
                <KeyInfo>
                    <X509Data>
                        <X509Certificate>x</X509Certificate>
                        <X509IssuerSerial>
                            <X509IssuerName>CN=x, DC=x</X509IssuerName>
                            <X509SerialNumber>1</X509SerialNumber>
                        </X509IssuerSerial>
                    </X509Data>
                </KeyInfo>
            </Signature>
            <saml2:Subject>
                <saml2:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified">x</saml2:NameID>
                <saml2:SubjectConfirmation Method="urn:oasis:names:tc:SAML:2.0:cm:bearer">
                    <saml2:SubjectConfirmationData NotBefore="2023-06-30T12:22:50.503Z" NotOnOrAfter="2023-06-30T13:22:37.503Z"/>
                </saml2:SubjectConfirmation>
            </saml2:Subject>
            <saml2:Conditions NotBefore="2023-06-30T12:22:50.503Z" NotOnOrAfter="2023-06-30T13:22:37.503Z"/>
            <saml2:AttributeStatement>
                <saml2:Attribute Name="consumerId" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
                    <saml2:AttributeValue>x</saml2:AttributeValue>
                </saml2:Attribute>
                <saml2:Attribute Name="auditTrackingId" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:uri">
                    <saml2:AttributeValue>x</saml2:AttributeValue>
                </saml2:Attribute>
            </saml2:AttributeStatement>
        </saml2:Assertion>
    </soapenv:Header>
    <soapenv:Body>
        <typ:kalkulatorForespoersel>
            <userSessionCorrelationID>x</userSessionCorrelationID>
            <organisasjonsnummer>1</organisasjonsnummer>
            <rettighetshaver>
                <foedselsnummer>1</foedselsnummer>
                <aarligInntektFoerUttak>1</aarligInntektFoerUttak>
                <uttaksperiode>
                    <startAlder>67</startAlder>
                    <startMaaned>1</startMaaned>
                    <grad>100</grad>
                    <aarligInntekt>1</aarligInntekt>
                </uttaksperiode>
                <antallInntektsaarEtterUttak>1</antallInntektsaarEtterUttak>
                <harAfp>false</harAfp>
                <oenskesSimuleringAvFolketrygd>false</oenskesSimuleringAvFolketrygd>
            </rettighetshaver>
        </typ:kalkulatorForespoersel>
    </soapenv:Body>
</soapenv:Envelope>"""

private const val RESPONSE_BODY =
    """<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
    <soap:Header/>
    <soap:Body wsu:Id="x" xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd">
        <ns2:pensjonsrettigheter xmlns:ns2="http://x.no/api/pensjonskalkulator/v3/typer">
            <pensjonsRettigheter>
                <avtalenummer>1</avtalenummer>
                <arbeidsgiver>x</arbeidsgiver>
                <selskapsnavn>x</selskapsnavn>
                <produktbetegnelse>x</produktbetegnelse>
                <kategori>x</kategori>
                <underkategori>x</underkategori>
                <innskuddssaldo>1</innskuddssaldo>
                <naavaerendeAvtaltAarligInnskudd>1</naavaerendeAvtaltAarligInnskudd>
                <avkastningsgaranti>false</avkastningsgaranti>
                <beregningsmodell>x</beregningsmodell>
                <startAlder>67</startAlder>
                <sluttAlder>77</sluttAlder>
                <utbetalingsperioder>
                    <startAlder>67</startAlder>
                    <startMaaned>1</startMaaned>
                    <sluttAlder>68</sluttAlder>
                    <sluttMaaned>1</sluttMaaned>
                    <aarligUtbetalingForventet>1</aarligUtbetalingForventet>
                    <grad>100</grad>
                </utbetalingsperioder>
                <opplysningsdato>2023-01-11</opplysningsdato>
            </pensjonsRettigheter>
            <pensjonsRettigheter>
                <selskapsnavn>x</selskapsnavn>
                <produktbetegnelse>x</produktbetegnelse>
                <kategori>x</kategori>
            </pensjonsRettigheter>
        </ns2:pensjonsrettigheter>
    </soap:Body>
</soap:Envelope>"""
