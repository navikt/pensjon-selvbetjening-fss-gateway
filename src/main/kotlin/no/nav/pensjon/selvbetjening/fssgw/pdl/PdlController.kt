package no.nav.pensjon.selvbetjening.fssgw.pdl

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class PdlController(private val jwsValidator: JwsValidator, private val pdlConsumer: PdlConsumer) {

    @PostMapping("pdl")
    fun pdlRequest(
            @RequestBody body: String,
            request: HttpServletRequest): ResponseEntity<String> {
        val auth : String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val callId: String? = request.getHeader("X-Correlation-ID")

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }

        val responseBody = pdlConsumer.callPdl(body, callId)
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        return ResponseEntity(responseBody, httpHeaders, HttpStatus.OK)
    }
}
