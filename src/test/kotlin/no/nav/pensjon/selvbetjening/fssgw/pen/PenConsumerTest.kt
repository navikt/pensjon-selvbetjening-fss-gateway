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
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
internal class PenConsumerTest : WebClientTest() {

    private lateinit var bodilessPenConsumer: BodilessPenConsumer

    @Mock
    lateinit var serviceUserTokenGetter: ServiceTokenGetter

    @BeforeEach
    fun initialize() {
        setUp()
        val token = ServiceTokenData("token", "type", LocalDateTime.MIN, 1L)
        Mockito.`when`(serviceUserTokenGetter.getServiceUserToken()).thenReturn(token)
        bodilessPenConsumer = BodilessPenConsumer(baseUrl(), serviceUserTokenGetter)
    }

    @Test
    fun `shall return data when OK`() {
        prepare(penDataResponse)
        val response = bodilessPenConsumer.callPen("", "id", "pid", HttpMethod.GET)
        assertNotNull(response)
    }

    @Test
    fun `shall throw PenException when PEN returns error`() {
        prepare(penErrorResponse)
        val exception: PenException = assertThrows(PenException::class.java) { bodilessPenConsumer.callPen("", "id", "pid", HttpMethod.GET) }
        assertEquals("Failed to access PEN at $baseUrl: 401 Unauthorized from ${HttpMethod.GET.name} $baseUrl | Response: oops", exception.message)
    }

    @Test
    fun `ping shall return data when PEN responds OK`() {
        prepare(penPingResponse)
        val response = bodilessPenConsumer.ping("path")
        assertEquals("Ok", response)
    }

    private val penDataResponse: MockResponse
        get() = jsonResponse()
                .setBody("""{
                  "foo": "bar"
                }""")

    private val penErrorResponse: MockResponse
        get() = jsonResponse(HttpStatus.UNAUTHORIZED)
                .setBody("oops")

    private val penPingResponse: MockResponse
        // Note: PEN uses content type 'JSON' despite plain-text body
        get() = jsonIso88591Response().setBody("Ok")
}
