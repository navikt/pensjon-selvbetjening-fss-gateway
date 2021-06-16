package no.nav.pensjon.selvbetjening.fssgw.tps

/**
 * Used for problems related to Tjenestebasert Persondatasystem (TPS) access.
 */
class TpsException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)
}
