package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NowProvider : TimeProvider {

    override fun time(): LocalDateTime = LocalDateTime.now()
}
