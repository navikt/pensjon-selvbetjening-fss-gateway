package no.nav.pensjon.selvbetjening.fssgw.pen

class PenException(message: String, cause: Throwable?) : Exception(message, cause) {

    constructor(message: String) : this(message, null)
}
