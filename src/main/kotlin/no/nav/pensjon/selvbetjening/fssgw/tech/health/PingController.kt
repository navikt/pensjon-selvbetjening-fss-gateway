package no.nav.pensjon.selvbetjening.fssgw.tech.health

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.servlet.http.HttpServletRequest

@RestController
@RequestMapping("internal")
class PingController {

    @GetMapping("ping")
    fun doGet(request: HttpServletRequest): ResponseEntity<String> {
        return ResponseEntity("pong", contentTypeHeaders(), HttpStatus.OK)
    }

    private fun contentTypeHeaders() = HttpHeaders().also { it.contentType = MediaType.TEXT_PLAIN }
}
