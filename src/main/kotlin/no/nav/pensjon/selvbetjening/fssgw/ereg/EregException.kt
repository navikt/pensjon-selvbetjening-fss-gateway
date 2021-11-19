package no.nav.pensjon.selvbetjening.fssgw.ereg

class EregException(message: String, cause: Throwable?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}