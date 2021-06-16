package no.nav.pensjon.selvbetjening.fssgw.mock

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.nio.charset.StandardCharsets

open class WebClientTest {

    protected lateinit var baseUrl: String
    private val server: MockWebServer = MockWebServer()

    fun setUp() {
        server.start()
        baseUrl = "http://localhost:${server.port}"
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    internal fun prepare(response: MockResponse?) {
        server.enqueue(response!!)
    }

    internal fun takeRequest(): RecordedRequest {
        return server.takeRequest()
    }

    fun jsonResponse(): MockResponse {
        return MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    }

    fun xmlResponse(): MockResponse {
        return MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8))
    }

    internal fun plaintextResponse(): MockResponse {
        return MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
    }

    internal fun jsonResponse(status: HttpStatus): MockResponse {
        return jsonResponse()
                .setResponseCode(status.value())
    }

    internal fun baseUrl(): String {
        return baseUrl
    }
}
