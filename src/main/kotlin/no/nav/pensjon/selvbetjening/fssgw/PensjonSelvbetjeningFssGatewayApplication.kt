package no.nav.pensjon.selvbetjening.fssgw

import io.prometheus.client.hotspot.DefaultExports
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PensjonSelvbetjeningFssGatewayApplication

fun main(args: Array<String>) {
    DefaultExports.initialize()
    runApplication<PensjonSelvbetjeningFssGatewayApplication>(*args)
}
