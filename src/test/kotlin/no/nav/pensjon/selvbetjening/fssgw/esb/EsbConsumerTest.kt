package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class EsbConsumerTest : WebClientTest() {

    private lateinit var consumer: EsbConsumer

    @BeforeEach
    fun initialize() {
        setUp()
        consumer = EsbConsumer(baseUrl())
    }

    @Test
    fun `getFullmakt shall return data when ESB responds ok`() {
        prepare(esbFullmaktResponse())
        val response = consumer.callEsb("path1", EsbXml.fullmaktRequestBody)
        assertEquals(EsbXml.fullmaktResponseBody, response)
    }

    @Test
    fun `getPerson shall return data when ESB responds ok`() {
        prepare(esbPersonResponse())
        val response = consumer.callEsb("path1", EsbXml.personRequestBody)
        assertEquals(EsbXml.personResponseBody, response)
    }

    @Test
    fun `ping shall return data when ESB responds ok`() {
        prepare(esbPingResponse())
        val response = consumer.callEsb("path1", EsbXml.pingRequestBody)
        assertEquals(EsbXml.pingResponseBody, response)
    }

    @Test
    fun `getPerson shall throw EsbException when ESB returns error`() {
        prepare(esbErrorResponse())
        val exception: EsbException = assertThrows(EsbException::class.java) { consumer.callEsb("path1", "{}") }
        assertEquals(
            "Failed to access ESB at $baseUrl: 401 Unauthorized from POST $baseUrl/path1 | Response: foo",
            exception.message
        )
    }

    private fun esbFullmaktResponse(): MockResponse {
        return xmlResponse().setBody(EsbXml.fullmaktResponseBody)
    }

    private fun esbPersonResponse(): MockResponse {
        return xmlResponse().setBody(EsbXml.personResponseBody)
    }

    private fun esbPingResponse(): MockResponse {
        return xmlResponse().setBody(EsbXml.pingResponseBody)
    }

    private fun esbErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED).setBody("foo")
    }
}
