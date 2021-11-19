package no.nav.pensjon.selvbetjening.fssgw.aareg

import io.jsonwebtoken.JwtException
import no.nav.pensjon.selvbetjening.fssgw.dkif.DkifController
import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2Exception
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/aareg")
class AaregController(private val jwsValidator: JwsValidator, private val aaregConsumer : AaregConsumer) {
    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/v1/arbeidstaker/arbeidsforhold")
    fun getArbeidsgivere(request: HttpServletRequest): ResponseEntity<String> {
        val auth: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val accessToken: String = auth?.substring("Bearer ".length) ?: ""
        val navCallId: String? = request.getHeader(NAV_CALL_ID)
        val personIdent: String = request.getHeader(NAV_PERSON_IDENT)

        try {
            jwsValidator.validate(accessToken)
        } catch (e: JwtException) {
            return unauthorized(e)
        } catch (e: Oauth2Exception) {
            return unauthorized(e)
        }

        val responseBody = aaregConsumer.getArbeidsgivere(navCallId, personIdent)
        return ResponseEntity(responseBody, jsonContentType, HttpStatus.OK)
    }

    private fun unauthorized(e: Exception): ResponseEntity<String> {
        log.error("Unauthorized: ${e.message}")
        return ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
    }

    private val jsonContentType: HttpHeaders
        get() {
            val httpHeaders = HttpHeaders()
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            return httpHeaders
        }

    companion object AaregControllerConstants{
        private const val NAV_CALL_ID = "Nav-Call-Id"
        private const val NAV_PERSON_IDENT = "Nav-Personident"
    }
}