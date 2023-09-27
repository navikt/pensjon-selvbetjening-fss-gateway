package no.nav.pensjon.selvbetjening.fssgw.common

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

class EgressException(
    message: String,
    val statusCode: HttpStatusCode = HttpStatus.BAD_GATEWAY,
    cause: Throwable? = null
) : RuntimeException(message, cause)
