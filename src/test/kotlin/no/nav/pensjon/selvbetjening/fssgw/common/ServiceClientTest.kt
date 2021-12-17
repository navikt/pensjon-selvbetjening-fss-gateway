package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.http.HttpHeaders

internal class ServiceClientTest : WebClientTest() {

    private lateinit var client: ServiceClient

    @BeforeEach
    fun initialize() {
        setUp()
        client = ServiceClient()
    }

    @Test
    fun callService_returns_data_when_ok() {
        prepare(response())
        val ingressHeaders = HashMap<String, String>()
        ingressHeaders[HttpHeaders.AUTHORIZATION] = "Bearer jwt"
        ingressHeaders["key"] = "value"

        val response = client.callService(baseUrl(), ingressHeaders)

        val egressHeaders = takeRequest().headers
        assertEquals("""{ "foo": "bar" }""", response)
        assertEquals("Bearer jwt", egressHeaders[HttpHeaders.AUTHORIZATION])
        assertEquals("value", egressHeaders["key"])
    }

    private fun response(): MockResponse {
        return jsonResponse().setBody("""{ "foo": "bar" }""")
    }
}
