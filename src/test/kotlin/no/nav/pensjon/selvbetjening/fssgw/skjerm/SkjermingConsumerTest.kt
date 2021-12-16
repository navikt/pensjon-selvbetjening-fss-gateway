package no.nav.pensjon.selvbetjening.fssgw.skjerm

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders

internal class SkjermingConsumerTest : WebClientTest() {

    private lateinit var consumer: SkjermingConsumer

    @BeforeEach
    fun initialize() {
        setUp()
        consumer = SkjermingConsumer(baseUrl())
    }

    @Test
    fun isSkjermet_returns_false_when_notSkjermet() {
        prepare(notSkjermetResponse())
        val ingressHeaders = HashMap<String, String>()
        ingressHeaders.put(HttpHeaders.HOST, "host1")
        ingressHeaders.put("foo", "bar")

        val response = consumer.isSkjermet("01023456789", ingressHeaders)

        val egressHeaders = takeRequest().headers
        assertTrue(response.equals("false"))
        assertEquals("host1", egressHeaders.get(HttpHeaders.HOST))
        assertEquals("bar", egressHeaders.get("foo"))
    }

    private fun notSkjermetResponse(): MockResponse {
        // Note that skjermede-personer-pip returns Content-Type: application/json
        // despite plain-text body
        return jsonResponse().setBody("false")
    }
}
