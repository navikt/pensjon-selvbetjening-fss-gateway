package no.nav.pensjon.selvbetjening.fssgw.tech.oidc

interface OidcConfigGetter {

    fun getIssuer(): String

    fun getAuthorizationEndpoint(): String

    fun getTokenEndpoint(): String

    fun getJsonWebKeySetUri(): String

    fun refresh()
}
