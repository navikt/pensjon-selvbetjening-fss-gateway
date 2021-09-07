package no.nav.pensjon.selvbetjening.fssgw.dkif

class DkifException(message: String, cause: Throwable?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}
