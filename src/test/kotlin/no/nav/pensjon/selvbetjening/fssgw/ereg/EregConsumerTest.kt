package no.nav.pensjon.selvbetjening.fssgw.ereg

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class EregConsumerTest : WebClientTest() {
    private lateinit var consumer: EregConsumer

    @BeforeEach
    fun initialize() {
        setUp()
        consumer = EregConsumer(baseUrl())
    }

    @Test
    fun `getOrganisasjonNoekkelinfo should return data when Ereg responds OK`() {
        prepare(eregDataResponse())
        val response = consumer.getOrganisasjonNoekkelinfo(null, "0000000000")
        assertNotNull(response)
    }

    @Test
    fun `getOrganisasjonNoekkelinfo should throw EregException when Ereg returns error`() {
        prepare(eregErrorResponse())
        val expectedUrl = baseUrl
        val expectedOrganisasjonsnummer = "0000000000"
        val exception: EregException = assertThrows(EregException::class.java) { consumer.getOrganisasjonNoekkelinfo(null, expectedOrganisasjonsnummer) }
        assertEquals("Failed to access Ereg at ${expectedUrl}: 401 Unauthorized from GET $expectedUrl/v1/organisasjon/$expectedOrganisasjonsnummer/noekkelinfo | Response: foo", exception.message)
    }

    private fun eregDataResponse(): MockResponse {
        return jsonResponse().setBody("{}")
    }

    private fun eregErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED)
                .setBody("foo")
    }
}