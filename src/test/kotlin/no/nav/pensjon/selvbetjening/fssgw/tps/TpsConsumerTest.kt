package no.nav.pensjon.selvbetjening.fssgw.tps

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
internal class TpsConsumerTest : WebClientTest() {

    private lateinit var consumer: TpsConsumer

    @BeforeEach
    fun initialize() {
        setUp()
        consumer = TpsConsumer(baseUrl())
    }

    @Test
    fun `getPerson shall return data when TPS responds ok`() {
        prepare(tpsPersonResponse())
        val response = consumer.getPerson(TpsXml.personRequestBody)
        assertEquals(TpsXml.personResponseBody, response)
    }

    @Test
    fun `ping shall return data when TPS responds ok`() {
        prepare(tpsPingResponse())
        val response = consumer.ping(TpsXml.pingRequestBody)
        assertEquals(TpsXml.pingResponseBody, response)
    }

    @Test
    fun `getPerson shall throw TpsException when TPS returns error`() {
        prepare(tpsErrorResponse())
        val exception: TpsException = assertThrows(TpsException::class.java) { consumer.getPerson("{}") }
        assertEquals("Failed to access TPS at $baseUrl: 401 Unauthorized from POST $baseUrl/nav-cons-pen-pselv-personWeb/sca/PSELVPersonWSEXP | Response: foo", exception.message)
    }

    private fun tpsPersonResponse(): MockResponse {
        return xmlResponse().setBody(TpsXml.personResponseBody)
    }

    private fun tpsPingResponse(): MockResponse {
        return xmlResponse().setBody(TpsXml.pingResponseBody)
    }

    private fun tpsErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED).setBody("foo")
    }
}
