package no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(MockitoExtension::class)
class MaskinportenClientTest {

    @Mock
    private lateinit var webClient: WebClient

    private lateinit var maskinportenClient: MaskinportenClient

    private var fetchTokenCallCount = 0

    @BeforeEach
    fun setUp() {
        // Set up the WebClient mock to return a MaskinportenToken
        maskinportenClient = object : MaskinportenClient(webClient, "clientId", "clientJwk", "issuer", "endpoint") {
            override fun fetchToken(scope: String): MaskinportenToken {
                // Return a mock token instead of performing real signing
                fetchTokenCallCount++
                return when (scope) {
                    "test-scope" -> MaskinportenToken(
                        access_token = "test-token",
                        token_type = "Bearer",
                        expires_in = 100,
                        "test-scope"
                    )

                    "another-scope" -> MaskinportenToken(
                        access_token = "another-token",
                        token_type = "Bearer",
                        expires_in = 100,
                        "another-scope"
                    )

                    else -> throw RuntimeException("Unexpected scope: $scope")
                }
            }
        }
    }

    @Test
    fun `should fetch token from cache when available`() {
        // First call, fetchToken should be called
        val countBeforeTestStart = fetchTokenCallCount
        val token1 = maskinportenClient.getToken("test-scope")
        assertEquals("test-token", token1.access_token)

        // Second call with the same scope, token should be retrieved from cache
        val token2 = maskinportenClient.getToken("test-scope")
        assertEquals("test-token", token2.access_token)

        // Assert that fetchToken was called only once
        assertEquals(
            countBeforeTestStart + 1,
            fetchTokenCallCount,
            "fetchToken should be called only once for 'test-scope'"
        )
    }

    @Test
    fun `should fetch new token when scope is different`() {
        // First call for "test-scope"
        val countBeforeTestStart = fetchTokenCallCount
        maskinportenClient.getToken("test-scope")

        // Call for a different scope, fetchToken should be called again
        val token = maskinportenClient.getToken("another-scope")
        assertEquals("another-token", token.access_token)

        // Assert that fetchToken was called twice in total (once for each scope)
        assertEquals(countBeforeTestStart + 2, fetchTokenCallCount, "fetchToken should be called once per unique scope")
    }
}