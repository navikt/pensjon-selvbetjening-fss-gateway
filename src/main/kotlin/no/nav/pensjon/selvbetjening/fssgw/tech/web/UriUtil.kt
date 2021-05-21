package no.nav.pensjon.selvbetjening.fssgw.tech.web

import java.net.URI
import java.net.URISyntaxException

object UriUtil {

    fun uriFrom(value: String): URI {
        val uri = URI(value)

        if (uri.port < 0) {
            throw URISyntaxException(value, "No URI port specified")
        }

        return uri
    }
}
