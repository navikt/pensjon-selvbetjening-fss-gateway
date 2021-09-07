package no.nav.pensjon.selvbetjening.fssgw.dkif

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
internal class DkifConsumerTest : WebClientTest(){
    private lateinit var consumer: DkifConsumer

    @Mock
    lateinit var serviceUserTokenGetter: ServiceTokenGetter

    @BeforeEach
    fun initialize() {
        setUp()
        val token = ServiceTokenData("token", "type", LocalDateTime.MIN, 1L)
        Mockito.`when`(serviceUserTokenGetter.getServiceUserToken()).thenReturn(token)
        consumer = DkifConsumer(baseUrl(), serviceUserTokenGetter)
    }

    @Test
    fun `getKontaktinfo should return data when Dkif responds OK`() {
        prepare(dkifDataResponse())
        val response = consumer.getKontaktinfo(null,"",false, "00000000000")
        assertNotNull(response)
    }

    @Test
    fun `getKontaktinfo should throw DkifException when Dkif returns error`() {
        prepare(dkifErrorResponse())
        val expectedUrl = "$baseUrl"
        val exception: DkifException = assertThrows(DkifException::class.java) { consumer.getKontaktinfo(null,"", false,"nb") }
        assertEquals("Failed to access Dkif at ${expectedUrl}: 401 Unauthorized from GET $expectedUrl?inkluderSikkerDigitalPost=false | Response: foo", exception.message)
    }

    private fun dkifDataResponse(): MockResponse {
        return jsonResponse().setBody("{}")
    }

    private fun dkifErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED)
                .setBody("foo")
    }
}