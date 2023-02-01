package no.nav.pensjon.selvbetjening.fssgw.common

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class XmlEscaperTest {

    @Test
    fun `escapeXml handles empty values`() {
        assertEquals("", XmlEscaper.escapeXml(null))
        assertEquals("", XmlEscaper.escapeXml(""))
        assertEquals(" ", XmlEscaper.escapeXml(" "))
    }

    @Test
    fun `escapeXml returns non-XML-reserved characters unchanged`() {
        assertEquals("abc_ÆØÅ-123", XmlEscaper.escapeXml("abc_ÆØÅ-123"))
    }

    @Test
    fun `escapeXml escapes XML-reserved characters`() {
        assertEquals("&lt;&quot;&amp;&apos;&gt;", XmlEscaper.escapeXml("<\"&\'>"))
    }

    @Test
    fun `escapeXml escapes already XML-escaped characters`() {
        assertEquals("&amp;lt;&amp;quot;&amp;amp;&amp;apos;&amp;gt;", XmlEscaper.escapeXml("&lt;&quot;&amp;&apos;&gt;"))
    }
}
