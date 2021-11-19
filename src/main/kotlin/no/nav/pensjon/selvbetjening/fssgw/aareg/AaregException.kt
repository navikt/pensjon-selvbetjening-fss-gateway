package no.nav.pensjon.selvbetjening.fssgw.aareg

class AaregException(message: String, cause: Throwable?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}