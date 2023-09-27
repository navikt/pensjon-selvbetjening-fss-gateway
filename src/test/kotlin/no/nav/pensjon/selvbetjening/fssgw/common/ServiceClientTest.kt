package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.util.*

internal class ServiceClientTest : WebClientTest() {

    private lateinit var client: ServiceClient

    @BeforeEach
    fun initialize() {
        setUp()
        client = ServiceClient()
    }

    @Test
    fun `doGet returns data when service returns data`() {
        prepare(dataResponse())

        val response = client.doGet(baseUrl(), ingressHeaders())

        assertEgressHeaders()
        assertEquals("""{ "foo": "bar" }""", response)
    }

    @Test
    fun `doGet returns empty string when no data from service`() {
        prepare(emptyResponse())

        val response = client.doGet(baseUrl(), ingressHeaders())

        assertEgressHeaders()
        assertEquals("", response)
    }

    @Test
    fun `doPost returns data when service returns data`() {
        prepare(dataResponse())

        val response = client.doPost(baseUrl(), ingressHeaders(), "body1")

        assertEgressHeaders()
        assertEquals("""{ "foo": "bar" }""", response)
    }

    @Test
    fun `doPost returns empty string when no data from service`() {
        prepare(emptyResponse())

        val response = client.doPost(baseUrl(), ingressHeaders(), "body1")

        assertEgressHeaders()
        assertEquals("", response)
    }

    @Test
    fun `doGet returns HTTP status code in exception when service returns 4xx`() {
        prepare(clientErrorResponse())

        val exception = assertThrows<EgressException> { client.doGet(baseUrl(), ingressHeaders()) }

        assertEquals("your bad", exception.message)
        assertEquals(HttpStatus.CONFLICT, exception.statusCode)
    }

    @Test
    fun `doPost returns HTTP status code in exception when service returns 5xx`() {
        prepare(serverErrorResponse())

        val exception = assertThrows<EgressException> { client.doPost(baseUrl(), ingressHeaders(), "body1") }

        assertEquals("my bad", exception.message)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.statusCode)
    }

    private fun ingressHeaders() =
        TreeMap<String, String>().apply {
            this[HttpHeaders.AUTHORIZATION] = "Bearer jwt"
            this["key"] = "value"
        }

    private fun dataResponse(): MockResponse = jsonResponse().setBody("""{ "foo": "bar" }""")

    private fun emptyResponse(): MockResponse = jsonResponse().setBody("") // makes webClient...block() return null

    private fun clientErrorResponse(): MockResponse = jsonResponse(HttpStatus.CONFLICT).setBody("your bad")

    private fun serverErrorResponse(): MockResponse = jsonResponse(HttpStatus.INTERNAL_SERVER_ERROR).setBody("my bad")

    private fun assertEgressHeaders() {
        val egressHeaders = takeRequest().headers
        assertEquals("Bearer jwt", egressHeaders[HttpHeaders.AUTHORIZATION])
        assertEquals("value", egressHeaders["key"])
    }
}
