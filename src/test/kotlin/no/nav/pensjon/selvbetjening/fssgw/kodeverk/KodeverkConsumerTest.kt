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
        prepare(journalforingDataResponse())
        val response = consumer.getBetydningerForPostnummer(null, "", "nb")
        assertNotNull(response)
    }

    @Test
    fun `getBetydningerForPostnummer shall throw KodeverkException when Kodeverk returns error`() {
        prepare(journalforingErrorResponse())
        val expectedUrl = "$baseUrl/Postnummer/koder/betydninger?spraak=nb"

        val exception: KodeverkException =
            assertThrows(KodeverkException::class.java) { consumer.getBetydningerForPostnummer(null, "", "nb") }

        assertEquals(
            "Failed to access Kodeverk at ${expectedUrl}: 500 Internal Server Error from GET $expectedUrl | Response: foo",
            exception.message
        )
    }

    private fun journalforingDataResponse(): MockResponse {
        return jsonResponse().setBody("{}")
    }

    private fun journalforingErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR)
            .setBody("foo")
    }
}
