package no.nav.pensjon.selvbetjening.fssgw.kodeverk

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

internal class KodeverkConsumerTest : WebClientTest() {

    private lateinit var consumer: KodeverkConsumer

    @BeforeEach
    fun initialize() {
        setUp()
        consumer = KodeverkConsumer(baseUrl())
    }

    @Test
    fun `getBetydningerForPostnummer shall return data when Kodeverk responds OK`() {
        prepare(kodeverkDataResponse())
        val response = consumer.getBetydningerForPostnummer(null, "", "nb")
        assertTrue(response.contains("OSLO"))
    }

    @Test
    fun `getBetydningerForPostnummer shall throw KodeverkException when Kodeverk returns error`() {
        prepare(kodeverkErrorResponse())
        val expectedUrl = "$baseUrl/Postnummer/koder/betydninger?spraak=nb"

        val exception: KodeverkException =
            assertThrows(KodeverkException::class.java) { consumer.getBetydningerForPostnummer(null, "", "nb") }

        assertEquals(
            "Failed to access Kodeverk at ${expectedUrl}: 500 Internal Server Error from GET $expectedUrl | Response: foo",
            exception.message
        )
    }

    private fun kodeverkDataResponse(): MockResponse {
        return jsonResponse().setBody("""
{
    "betydninger": {
        "0507": [
            {
                "gyldigFra": "2014-01-06",
                "gyldigTil": "9999-12-31",
                "beskrivelser": {
                    "nb": {
                        "term": "OSLO",
                        "tekst": "OSLO"
                    }
                }
            }
        ],
        "8488": [
            {
                "gyldigFra": "1900-01-01",
                "gyldigTil": "9999-12-31",
                "beskrivelser": {
                    "nb": {
                        "term": "NØSS",
                        "tekst": "NØSS"
                    }
                }
            }
        ],
        "4957": [
            {
                "gyldigFra": "2016-09-15",
                "gyldigTil": "9999-12-31",
                "beskrivelser": {
                    "nb": {
                        "term": "RISØR",
                        "tekst": "RISØR"
                    }
                }
            }
        ]
    }
}            
        """.trimMargin())
    }

    private fun kodeverkErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR)
            .setBody("foo")
    }
}
