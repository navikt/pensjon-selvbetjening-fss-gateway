package no.nav.pensjon.selvbetjening.fssgw.tech.oidc

/**
 * Used for problems related to Open ID Connect (OIDC).
 */
class OidcException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)
}
