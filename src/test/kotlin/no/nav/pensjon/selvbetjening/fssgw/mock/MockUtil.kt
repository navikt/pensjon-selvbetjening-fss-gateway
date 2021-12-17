package no.nav.pensjon.selvbetjening.fssgw.mock

import no.nav.pensjon.selvbetjening.fssgw.tech.sts.ServiceTokenData
import org.mockito.Mockito
import java.time.LocalDateTime

object MockUtil {

    /**
     * Hack to make Mockito argument matchers work with Kotlin
     */
    fun <T> anyObject(): T {
        Mockito.anyObject<T>()
        return null as T
    }

    fun serviceTokenData(): ServiceTokenData = ServiceTokenData(
        "j.w.t",
        "JWT", LocalDateTime.now(),
        60L)
}
