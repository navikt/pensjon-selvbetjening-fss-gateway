package no.nav.pensjon.selvbetjening.fssgw.ereg

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.JwsValidator
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(EregController::class)
internal class EregControllerTest{
    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @MockBean
    lateinit var eregConsumer: EregConsumer

    @Test
    fun `Ereg request results in JSON response`() {
        val expectedIdent = "00000000000"
        Mockito.`when`(eregConsumer.getOrganisasjonNoekkelinfo(null, expectedIdent)).thenReturn("""{ "response": "bar"}""")

        mvc.perform(MockMvcRequestBuilders
                .get("/api/ereg/v1/organisasjon/$expectedIdent/noekkelinfo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                .content("foo"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().json("{'response':'bar'}"))
    }
}