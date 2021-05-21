package no.nav.pensjon.selvbetjening.fssgw.tech.web

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.net.URISyntaxException

internal class UriUtilTest {

    @Test
    fun `uriFrom URL with port string produces URI`() {
        val uri = UriUtil.uriFrom("http://www.example.net:80")
        assertEquals("www.example.net", uri.host)
        assertEquals(80, uri.port)
    }

    @Test
    fun `uriFrom URL without port causes URISyntaxException`() {
        val exception = assertThrows(URISyntaxException::class.java) { UriUtil.uriFrom("http://www.example.net")}
        assertEquals("No URI port specified: http://www.example.net", exception.message)
    }

    @Test
    fun `uriFrom non-URI string causes URISyntaxException`() {
        val exception = assertThrows(URISyntaxException::class.java) { UriUtil.uriFrom("invalid")}
        assertEquals("No URI port specified: invalid", exception.message)
    }
}
