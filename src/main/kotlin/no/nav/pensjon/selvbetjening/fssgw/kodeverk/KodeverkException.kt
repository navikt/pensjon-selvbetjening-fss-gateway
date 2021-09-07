package no.nav.pensjon.selvbetjening.fssgw.kodeverk

class KodeverkException(message: String, cause: Throwable?) : Exception(message, cause){
    constructor(message: String) : this(message, null)
}