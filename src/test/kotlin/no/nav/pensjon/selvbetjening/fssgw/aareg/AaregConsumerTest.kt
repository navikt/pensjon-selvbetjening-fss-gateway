package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
internal class AaregConsumerTest : WebClientTest(){
    private lateinit var consumer: AaregConsumer

    @Mock
    lateinit var serviceUserTokenGetter: ServiceTokenGetter

    @BeforeEach
    fun initialize() {
        setUp()
        val token = ServiceTokenData("token", "type", LocalDateTime.MIN, 1L)
        Mockito.`when`(serviceUserTokenGetter.getServiceUserToken()).thenReturn(token)
        consumer = AaregConsumer(baseUrl(), serviceUserTokenGetter)
    }

    @Test
    fun `getArbeidsgivere should return data when AAREG responds OK`() {
        prepare(aaregOkResponse())
        val response = consumer.getArbeidsgivere(null,"")
        assertNotNull(response)
    }

    @Test
    fun `getArbeidsgivere should throw AaregException when AAREG returns error`() {
        prepare(aaregBadTokenResponse())
        val expectedUrl = baseUrl
        val exception: AaregException = assertThrows(AaregException::class.java) { consumer.getArbeidsgivere(null,"") }
        assertEquals("""Failed to access Aareg at ${expectedUrl}: 403 Forbidden from GET ${expectedUrl}/aareg-services/api/v1/arbeidstaker/arbeidsforhold | Response: <html>

<head>
	<title>Error</title>
</head>

<body>Access Denied</body>

</html>""", exception.message)
    }

    private fun aaregOkResponse(): MockResponse {
        return jsonResponse(HttpStatus.OK)
            .setBody(
                """[
    {
        "navArbeidsforholdId": 1234567,
        "arbeidsforholdId": "1",
        "arbeidstaker": {
            "type": "Person",
            "offentligIdent": "01023456789",
            "aktoerId": "1234567890123"
        },
        "arbeidsgiver": {
            "type": "Person",
            "offentligIdent": "12345678901",
            "aktoerId": "2345678901234"
        },
        "opplysningspliktig": {
            "type": "Person",
            "offentligIdent": "12345678901",
            "aktoerId": "2345678901234"
        },
        "type": "ordinaertArbeidsforhold",
        "ansettelsesperiode": {
            "periode": {
                "fom": "2001-04-17"
            },
            "bruksperiode": {
                "fom": "2021-04-17T11:10:56.035"
            },
            "sporingsinformasjon": {
                "opprettetTidspunkt": "2021-04-17T11:10:56.036",
                "opprettetAv": "srvtestnorge-aareg",
                "opprettetKilde": "AAREG",
                "opprettetKildereferanse": "DOLLY",
                "endretTidspunkt": "2021-04-17T11:10:56.036",
                "endretAv": "srvtestnorge-aareg",
                "endretKilde": "AAREG",
                "endretKildereferanse": "DOLLY"
            }
        },
        "arbeidsavtaler": [
            {
                "type": "Ordinaer",
                "arbeidstidsordning": "ikkeSkift",
                "yrke": "5141103",
                "stillingsprosent": 100.0,
                "antallTimerPrUke": 37.5,
                "beregnetAntallTimerPrUke": 37.5,
                "bruksperiode": {
                    "fom": "2021-04-17T11:10:56.035"
                },
                "gyldighetsperiode": {
                    "fom": "2001-04-01"
                },
                "sporingsinformasjon": {
                    "opprettetTidspunkt": "2021-04-17T11:10:56.036",
                    "opprettetAv": "srvtestnorge-aareg",
                    "opprettetKilde": "AAREG",
                    "opprettetKildereferanse": "DOLLY",
                    "endretTidspunkt": "2021-04-17T11:10:56.036",
                    "endretAv": "srvtestnorge-aareg",
                    "endretKilde": "AAREG",
                    "endretKildereferanse": "DOLLY"
                }
            }
        ],
        "varsler": [
            {
                "entitet": "ARBEIDSFORHOLD",
                "varslingskode": "NAVEND"
            }
        ],
        "innrapportertEtterAOrdningen": true,
        "registrert": "2021-04-17T11:10:56.005",
        "sistBekreftet": "2021-04-17T11:10:56",
        "sporingsinformasjon": {
            "opprettetTidspunkt": "2021-04-17T11:10:56.036",
            "opprettetAv": "srvtestnorge-aareg",
            "opprettetKilde": "AAREG",
            "opprettetKildereferanse": "DOLLY",
            "endretTidspunkt": "2021-04-17T11:10:56.036",
            "endretAv": "srvtestnorge-aareg",
            "endretKilde": "AAREG",
            "endretKildereferanse": "DOLLY"
        }
    }
]""")
    }

    private fun aaregBadTokenResponse(): MockResponse {
        return htmlResponse(HttpStatus.FORBIDDEN)
                .setBody("""<html>

<head>
	<title>Error</title>
</head>

<body>Access Denied</body>

</html>""")
    }
}
