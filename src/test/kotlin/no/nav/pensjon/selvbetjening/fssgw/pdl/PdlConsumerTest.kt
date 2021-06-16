package no.nav.pensjon.selvbetjening.fssgw.pdl

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
internal class PdlConsumerTest : WebClientTest() {

    private lateinit var consumer: PdlConsumer

    @Mock
    lateinit var serviceUserTokenGetter: ServiceTokenGetter

    @BeforeEach
    fun initialize() {
        setUp()
        val token = ServiceTokenData("token", "type", LocalDateTime.MIN, 1L)
        Mockito.`when`(serviceUserTokenGetter.getServiceUserToken()).thenReturn(token)
        consumer = PdlConsumer(baseUrl(), serviceUserTokenGetter)
    }

    @Test
    fun `callPdl shall return data when PDL responds OK`() {
        prepare(pdlDataResponse())
        val response = consumer.callPdl("{}", "id")
        assertNotNull(response)
    }

    @Test
    fun `callPdl shall throw PdlException when PDL returns error`() {
        prepare(pdlErrorResponse())
        val exception: PdlException = assertThrows(PdlException::class.java) { consumer.callPdl("{}", "id") }
        assertEquals("Failed to access PDL at $baseUrl: 401 Unauthorized from POST $baseUrl | Response: foo", exception.message)
    }

    private fun pdlDataResponse(): MockResponse {
        return jsonResponse()
                .setBody("""{
  "data": {
    "hentPerson": {
      "foedsel": [{
        "foedselsaar": "null",
        "foedselsdato": "2001-01-01"
      }]
    }
  }
}""")
    }

    private fun pdlErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED)
                .setBody("foo")
    }
}
