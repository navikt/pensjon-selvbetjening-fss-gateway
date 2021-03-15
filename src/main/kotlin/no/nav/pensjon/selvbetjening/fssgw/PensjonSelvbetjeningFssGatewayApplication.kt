package no.nav.pensjon.selvbetjening.fssgw

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PensjonSelvbetjeningFssGatewayApplication

fun main(args: Array<String>) {
    runApplication<PensjonSelvbetjeningFssGatewayApplication>(*args)
}
