package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

import no.nav.pensjon.selvbetjening.fssgw.mock.WebClientTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

internal class MultiIssuerSupportTest : WebClientTest() {

    private lateinit var support: MultiIssuerSupport

    @BeforeEach
    fun initialize() {
        setUp()

        support = MultiIssuerSupport(
                Oauth2BasicData(baseUrl, "audience1"),
                Oauth2BasicData(baseUrl, "audience2"))
    }

    @Test
    fun `test that correct handler is returned and cached`() {
        `test that first handler is returned for first issuer`()
        `test that first handler is cached`()
        `test that second handler is returned for second issuer`()
        `test that both handlers are cached`()
        `test that OAuth2 exception is thrown when no issuers match`()
    }

    private fun `test that first handler is returned for first issuer`() {
        prepareResponse(1)

        val handler = support.getOauth2HandlerForIssuer("issuer1")

        assertEquals("audience1", handler.acceptedAudience)
        assertEquals("https://example1.io/authorization", handler.configGetter.getAuthorizationEndpoint())
    }

    private fun `test that second handler is returned for second issuer`() {
        prepareResponse(1)
        prepareResponse(2)

        val handler = support.getOauth2HandlerForIssuer("issuer2")

        assertEquals("audience2", handler.acceptedAudience)
        assertEquals("https://example2.io/authorization", handler.configGetter.getAuthorizationEndpoint())
    }

    private fun `test that first handler is cached`() {
        // Will fail if not cached, since no web server response is queued
        val handler = support.getOauth2HandlerForIssuer("issuer1")

        assertEquals("audience1", handler.acceptedAudience)
        assertEquals("https://example1.io/authorization", handler.configGetter.getAuthorizationEndpoint())
    }

    private fun `test that both handlers are cached`() {
        val handler1 = support.getOauth2HandlerForIssuer("issuer1")
        val handler2 = support.getOauth2HandlerForIssuer("issuer2")

        assertEquals("audience1", handler1.acceptedAudience)
        assertEquals("https://example1.io/authorization", handler1.configGetter.getAuthorizationEndpoint())
        assertEquals("audience2", handler2.acceptedAudience)
        assertEquals("https://example2.io/authorization", handler2.configGetter.getAuthorizationEndpoint())
    }

    private fun `test that OAuth2 exception is thrown when no issuers match`() {
        prepareResponse(1)
        prepareResponse(2)

        val exception = assertThrows(Oauth2Exception::class.java) { support.getOauth2HandlerForIssuer("issuer3") }

        assertEquals("Invalid issuer 'issuer3'", exception.message)
    }

    private fun prepareResponse(issuerNumber: Int) {
        prepare(jsonResponse().setBody("""{
      "issuer" : "issuer$issuerNumber",
      "authorization_endpoint" : "https://example$issuerNumber.io/authorization",
      "token_endpoint" : "https://example$issuerNumber.io/token",
      "jwks_uri" : "https://example$issuerNumber.io/jwks",
      "grant_types_supported" : [ "urn:ietf:params:oauth:grant-type:token-exchange" ],
      "token_endpoint_auth_methods_supported" : [ "private_key_jwt" ],
      "token_endpoint_auth_signing_alg_values_supported" : [ "RS256" ],
      "subject_types_supported" : [ "public" ]
    }"""))
    }
}
