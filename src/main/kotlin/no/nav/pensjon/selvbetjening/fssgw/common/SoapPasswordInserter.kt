package no.nav.pensjon.selvbetjening.fssgw.common

import no.nav.pensjon.selvbetjening.fssgw.common.XmlEscaper.escapeXml

/**
 * IMPORTANT: Calls utilizing the SoapPasswordInserter MUST be access-protected by other means than via SOAP Security
 *            (e.g. via Azure AD client credentials, Azure AD on-behalf-of or TokenX).
 *
 * SoapPasswordInserter replaces a placeholder Password element with a Password element containing the actual password.
 * The Password element is part of the SOAP Security header.
 *
 * The rationale behind this is to avoid having SOAP Security passwords in public cloud (GCP).
 *
 * Security measures to avoid password-revealing injection:
 * - The entire Password element is replaced (not just the password placeholder)
 * - It is checked that the Password element is within the UsernameToken element
 *   (which in turn must be withing the Header element)
 * - Only the first occurrence of the Password element is replaced
 */
object SoapPasswordInserter {

    private val placeholderElement: String = passwordElement("__password__")

    fun insertPassword(body: String, password: String) = if (isPasswordPlaceholderInUsernameToken(body))
        body.replaceFirst(placeholderElement, passwordElement(escapeXml(password))) else body

    private fun passwordElement(password: String) =
        "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\">$password</wsse:Password>"

    private fun isPasswordPlaceholderInUsernameToken(body: String): Boolean {
        val startIndex = body.indexOf(placeholderElement)

        if (startIndex < 0) {
            return false
        }

        val usernameTokenIndex = body.indexOf("</wsse:UsernameToken>")
        return startIndex < usernameTokenIndex && isUsernameTokenInHeader(body, usernameTokenIndex)
    }

    private fun isUsernameTokenInHeader(body: String, usernameTokenIndex: Int): Boolean {
        return 0 < usernameTokenIndex && usernameTokenIndex < body.indexOf("</soapenv:Header>", usernameTokenIndex)
    }
}
