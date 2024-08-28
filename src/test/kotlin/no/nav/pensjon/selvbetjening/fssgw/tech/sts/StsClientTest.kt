package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import okhttp3.mockwebserver.MockResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
internal class StsClientTest : WebClientTest() {

    private lateinit var consumer: StsClient
    private val issuedAt = LocalDateTime.of(2021, 2, 3, 4, 5, 6)

    @Mock
    lateinit var expirationChecker: ExpirationChecker

    @BeforeEach
    fun initialize() {
        setUp()
        `when`(expirationChecker.time()).thenReturn(issuedAt)

        consumer = StsClient(
            expirationChecker,
            baseUrl(),
            serviceUsername1 = "username1",
            servicePassword1 = "password1",
            serviceUsername2 = "username2",
            servicePassword2 = "password2",
            serviceUsername3 = "username3",
            servicePassword3 = "password3")
    }

    @Test
    fun getServiceUserToken_returns_tokenData_when_ok() {
        prepare(response1())

        val token: ServiceTokenData = consumer.getServiceUserToken(serviceUserId = 1)

        assertEquals("j.w.t", token.accessToken)
        assertEquals(3600L, token.expiresInSeconds)
        assertEquals(issuedAt, token.issuedTime)
        assertEquals("Bearer", token.tokenType)
    }

    @Test
    fun getServiceUserToken_caches_tokenData() {
        prepare(response1())

        val token: ServiceTokenData = consumer.getServiceUserToken(serviceUserId = 1)
        // Next line will fail if not cached, since only one response is queued:
        val cachedToken: ServiceTokenData = consumer.getServiceUserToken(serviceUserId = 1)

        assertEquals("j.w.t", token.accessToken)
        assertEquals("j.w.t", cachedToken.accessToken)
    }

    @Test
    fun getServiceUserToken_fetches_newTokenData_when_cacheIsExpired() {
        prepare(response1())
        prepare(response2())

        val token1: ServiceTokenData = consumer.getServiceUserToken(serviceUserId = 1)
        expireToken()
        val token2: ServiceTokenData = consumer.getServiceUserToken(serviceUserId = 1)

        assertEquals("j.w.t", token1.accessToken)
        assertEquals("jj.ww.tt", token2.accessToken)
        assertEquals(1800L, token2.expiresInSeconds)
        assertEquals(issuedAt, token2.issuedTime)
        assertEquals("Bearer", token2.tokenType)
    }

    private fun expireToken() {
        `when`(expirationChecker.isExpired(issuedAt, 3600)).thenReturn(true)
    }

    private fun response1(): MockResponse =
        jsonResponse().setBody(
            """{
    "access_token": "j.w.t",
    "token_type": "Bearer",
    "expires_in": 3600
}""")

    private fun response2(): MockResponse =
        jsonResponse().setBody(
            """{
    "access_token": "jj.ww.tt",
    "token_type": "Bearer",
    "expires_in": 1800
}""")
}
