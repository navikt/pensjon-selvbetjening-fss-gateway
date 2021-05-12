package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

/**
 * Used for problems related to OAuth 2.0.
 */
class Oauth2Exception(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)
}
