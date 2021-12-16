package no.nav.pensjon.selvbetjening.fssgw.common

class ConsumerException(message: String, cause: Throwable?) : Exception(message, cause) {
    constructor(message: String) : this(message, null)
}
