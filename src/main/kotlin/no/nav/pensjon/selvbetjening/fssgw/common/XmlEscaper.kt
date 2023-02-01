package no.nav.pensjon.selvbetjening.fssgw.common

object XmlEscaper {

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
