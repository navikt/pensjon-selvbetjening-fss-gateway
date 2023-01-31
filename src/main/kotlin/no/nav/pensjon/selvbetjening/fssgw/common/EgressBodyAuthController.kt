package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 * Controller used for requests where credentials are to be inserted into the request body.
 * Typically used for SOAP requests (where the request body consists of a SOAP envelope containing a
 * SOAP security header with credentials).
 * Credentials are inserted by replacing the placeholder "__password__" with the actual password.
 */
abstract class EgressBodyAuthController(
    ingressTokenValidator: JwsValidator,
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String,
    password: String)
    : TokenProtectedController(ingressTokenValidator, serviceClient, callIdGenerator, egressEndpoint) {

    private val replacement: String = ">${escapeXml(password)}</"

    override fun provideBodyAuth(body: String) = body.replace(">__password__</", replacement)

    override fun provideHeaderAuth(request: HttpServletRequest, headers: TreeMap<String, String>) {
        // No auth header – auth is instead provided in SOAP header (in HTTP body)
    }

    companion object {
        fun escapeXml(text: String?): String {
            if (text == null) {
                return ""
            }

            val builder = StringBuilder()

            for (element in text) {
                when (element) {
                    '<' -> builder.append("&lt;")
                    '>' -> builder.append("&gt;")
                    '\"' -> builder.append("&quot;")
                    '\'' -> builder.append("&apos;")
                    '&' -> builder.append("&amp;")
                    else -> builder.append(element)
                }
            }

            return builder.toString()
        }
    }
}
