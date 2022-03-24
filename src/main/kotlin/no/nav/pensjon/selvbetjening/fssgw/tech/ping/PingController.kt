package no.nav.pensjon.selvbetjening.fssgw.tech.ping

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/internal")
class PingController {

    @GetMapping("ping")
    fun ping(): ResponseEntity<String> {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        return ResponseEntity("{ \"reponse\": \"pong\" }", httpHeaders, HttpStatus.OK)
    }
}
