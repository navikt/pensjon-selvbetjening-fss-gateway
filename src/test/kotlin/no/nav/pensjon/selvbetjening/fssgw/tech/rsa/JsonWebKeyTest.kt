package no.nav.pensjon.selvbetjening.fssgw.tech.rsa

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class JsonWebKeyTest {

    private val key = JsonWebKey("a80c5e17-ab45-4300-8312-e0913241a198",
            "sig",
            "nOjPysCXf8Yk40DsQ6EZWD6pwJGqcKs2mI-v_T-soAJPEpDLzPC1KochdnmYCuBpnuekw_6bJujm1Xu" +
                    "-3iON5F6ZWqfJXuiGzcHsv3WksySJkjSqriHphb3j7097-5bLDQzTqeQU5BKZ" +
                    "-xnLJNAiTWZK_khgy1H2Oynj8y3KOMmpORgHnxx3rVCB2-iyEqTvT4VZ-wHhBJsTs8FAxY2vL3IXaAP_UeKpIVXtM70I_" +
                    "-ECe2pLFb39vvK8ey7eXbhhS-5wAw96KQ5K8Ly" +
                    "-JleaUUe89xbdk14TylpL18SDaM7QyY3ajO5i33M6WcEjqbWZoyjct3ZDWD3VvdohJPV4Tw",
            "AQAB")

    @Test
    fun `getKeyId returns key ID`() {
        assertEquals("a80c5e17-ab45-4300-8312-e0913241a198", key.getKeyId())
    }

    @Test
    fun `getRsaPublicKey returns RSA public key`() {
        val key = key.getRsaPublicKey()
        assertEquals("RSA", key.algorithm)
    }
}
