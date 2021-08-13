package no.nav.pensjon.selvbetjening.fssgw.journalforing

class JournalforingException(message: String, cause: Throwable?) : Exception(message, cause){
    constructor(message: String) : this(message, null)
}