package no.nav.pensjon.selvbetjening.fssgw.aareg

import no.nav.pensjon.selvbetjening.fssgw.dkif.DkifConsumer
import no.nav.pensjon.selvbetjening.fssgw.dkif.DkifException
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
internal class AaregConsumerTest : WebClientTest(){
    private lateinit var consumer: AaregConsumer

    @Mock
    lateinit var serviceUserTokenGetter: ServiceTokenGetter

    @BeforeEach
    fun initialize() {
        setUp()
        val token = ServiceTokenData("token", "type", LocalDateTime.MIN, 1L)
        Mockito.`when`(serviceUserTokenGetter.getServiceUserToken()).thenReturn(token)
        consumer = AaregConsumer(baseUrl(), serviceUserTokenGetter)
    }

    @Test
    fun `getArbeidsgivere should return data when Aareg responds OK`() {
        prepare(aaregDataResponse())
        val response = consumer.getArbeidsgivere(null,"")
        assertNotNull(response)
    }

    @Test
    fun `getArbeidsgivere should throw AaregException when Aareg returns error`() {
        prepare(aaregErrorResponse())
        val expectedUrl = baseUrl
        val exception: AaregException = assertThrows(AaregException::class.java) { consumer.getArbeidsgivere(null,"") }
        assertEquals("Failed to access Aareg at ${expectedUrl}: 401 Unauthorized from GET $expectedUrl/v1/arbeidstaker/arbeidsforhold | Response: foo", exception.message)
    }

    private fun aaregDataResponse(): MockResponse {
        return jsonResponse().setBody("{}")
    }

    private fun aaregErrorResponse(): MockResponse {
        return jsonResponse(HttpStatus.UNAUTHORIZED)
                .setBody("foo")
    }
}