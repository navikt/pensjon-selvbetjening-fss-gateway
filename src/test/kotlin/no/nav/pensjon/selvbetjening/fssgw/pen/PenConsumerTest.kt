package no.nav.pensjon.selvbetjening.fssgw.pen

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenData
import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenGetter
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
internal class PenConsumerTest : WebClientTest() {

    private lateinit var consumer: PenConsumer

    @Mock
    lateinit var serviceUserTokenGetter: ServiceTokenGetter

    @BeforeEach
    fun initialize() {
        setUp()
        val token = ServiceTokenData("token", "type", LocalDateTime.MIN, 1L)
        Mockito.`when`(serviceUserTokenGetter.getServiceUserToken()).thenReturn(token)
        consumer = PenConsumer(baseUrl(), serviceUserTokenGetter)
    }

    @Test
    fun shall_return_data_when_ok() {
        prepare(penDataResponse())
        val response = consumer.callPen("", "{}", "id", "pid")
        assertNotNull(response)
    }

    @Test
    fun shall_throwPenException_when_pen_returns_error() {
        prepare(penErrorResponse())
        val exception: PenException = assertThrows(PenException::class.java) { consumer.callPen("","{}", "id", "pid") }
        assertEquals("Failed to access PEN at $baseUrl: 401 Unauthorized from POST $baseUrl | Response: oops", exception.message)
    }

    private fun penDataResponse(): MockResponse {
        return jsonResponse()
                .setBody("""{
  "foo": "bar"
}""")
    }

    private fun penErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED)
                .setBody("oops")
    }
}
