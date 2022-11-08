package no.nav.pensjon.selvbetjening.fssgw.common

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CallIdGenerator {

    fun newCallId(): String = UUID.randomUUID().toString()
}
