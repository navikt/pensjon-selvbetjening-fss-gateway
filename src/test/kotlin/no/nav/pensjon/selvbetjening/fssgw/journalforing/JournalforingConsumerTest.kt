package no.nav.pensjon.selvbetjening.fssgw.journalforing

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
internal class JournalforingConsumerTest : WebClientTest(){

    private lateinit var consumer: JournalforingConsumer

    @Mock
    lateinit var serviceUserTokenGetter: ServiceTokenGetter

    @BeforeEach
    fun initialize() {
        setUp()
        val token = ServiceTokenData("token", "type", LocalDateTime.MIN, 1L)
        Mockito.`when`(serviceUserTokenGetter.getServiceUserToken()).thenReturn(token)
        consumer = JournalforingConsumer(baseUrl(), serviceUserTokenGetter)
    }

    @Test
    fun `opprettJournalpost shall return data when Journalforing responds OK`() {
        prepare(journalforingDataResponse())
        val response = consumer.opprettJournalpost("{}", "id", true)
        assertNotNull(response)
    }

    @Test
    fun `opprettJournalpost shall throw JournalforingException when Journalforing returns error`() {
        prepare(journalforingErrorResponse())
        val exception: JournalforingException = assertThrows(JournalforingException::class.java) { consumer.opprettJournalpost("{}", "id", true) }
        assertEquals("Failed to access Journalforing at $baseUrl: 401 Unauthorized from POST $baseUrl?forsoekFerdigstill=true | Response: foo", exception.message)
    }

    private fun journalforingDataResponse(): MockResponse {
        return jsonResponse().setBody("{}")
    }

    private fun journalforingErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED)
                .setBody("foo")
    }
}