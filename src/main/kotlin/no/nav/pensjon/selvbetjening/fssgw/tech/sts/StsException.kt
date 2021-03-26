package no.nav.pensjon.selvbetjening.fssgw.tech.sts

/**
 * Used for problems related to Security Token Service (STS) access.
 */
class StsException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)
}
