package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpHeaders
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

    private fun ingressHeaders(): TreeMap<String, String> {
        val headers = TreeMap<String, String>()
        headers[HttpHeaders.AUTHORIZATION] = "Bearer jwt"
        headers["key"] = "value"
        return headers
    }

    private fun dataResponse(): MockResponse {
        return jsonResponse().setBody("""{ "foo": "bar" }""")
    }

    private fun emptyResponse(): MockResponse {
        return jsonResponse().setBody("") // makes webClient...block() return null
    }

    private fun assertEgressHeaders() {
        val egressHeaders = takeRequest().headers
        assertEquals("Bearer jwt", egressHeaders[HttpHeaders.AUTHORIZATION])
        assertEquals("value", egressHeaders["key"])
    }
}
