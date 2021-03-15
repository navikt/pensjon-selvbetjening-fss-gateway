package no.nav.pensjon.selvbetjening.fssgw.tech.health

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class HealthController {

    @GetMapping("isAlive")
    fun isAlive(): String = "Alive"

    @GetMapping("isReady")
    fun isReady() = "Ready"
}
