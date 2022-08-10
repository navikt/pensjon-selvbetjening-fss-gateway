package no.nav.pensjon.selvbetjening.fssgw.tech.basicauth

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import java.lang.IllegalArgumentException
import java.nio.charset.StandardCharsets
import java.util.*

internal class BasicAuthValidatorTest {

    private lateinit var validator: BasicAuthValidator

    @BeforeEach
    fun initialize() {
        validator = BasicAuthValidator("user1", "secret")
    }

    @Test
    fun `validate returns true if correct username and password`() {
        val credentials = basicAuth("user1:secret")
        assertTrue(validator.validate(credentials))
    }

    @Test
    fun `validate returns false if wrong password`() {
        val credentials = basicAuth("user1:secreT")
        assertFalse(validator.validate(credentials))
    }

    @Test
    fun `validate returns false if wrong username`() {
        val credentials = basicAuth("user0:secret")
        assertFalse(validator.validate(credentials))
    }

    @Test
    fun `validate returns false if wrong delimiter`() {
        val credentials = basicAuth("user1;secret")
        assertFalse(validator.validate(credentials))
    }

    @Test
    fun `validate returns false if no credentials`() {
        assertFalse(validator.validate(" "))
        assertFalse(validator.validate(""))
    }

    @Test
    fun `validate throws IllegalArgumentException if credentials not Base64-encoded`() {
        val credentials = "user1:secret"
        assertThrows(IllegalArgumentException::class.java) { validator.validate(credentials) }
    }

    private fun basicAuth(credentials: String) =
        String(Base64.getEncoder().encode(credentials.toByteArray(StandardCharsets.UTF_8)))
}
