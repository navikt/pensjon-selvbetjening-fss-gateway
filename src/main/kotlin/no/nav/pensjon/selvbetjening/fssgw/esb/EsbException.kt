package no.nav.pensjon.selvbetjening.fssgw.esb

/**
 * Used for problems related to Enterprise Service Bus (ESB) access.
 */
class EsbException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)
}
