package no.nav.pensjon.selvbetjening.fssgw.kodeverk

import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import no.nav.pensjon.selvbetjening.fssgw.mock.MockUtil.anyObject
import no.nav.pensjon.selvbetjening.fssgw.tech.basicauth.BasicAuthValidator
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(KodeverkPingController::class)
internal class KodeverkPingControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var authValidator: BasicAuthValidator

    @MockBean
    lateinit var serviceClient: ServiceClient

    @Test
    fun `Kodeverk ping request results in JSON response`() {
        `when`(serviceClient.doGet(anyObject(), anyObject())).thenReturn("""{ "response": "bar"}""")
        val credentials = "cred"
        `when`(authValidator.validate(credentials)).thenReturn(true)

        mvc.perform(
            get("/api/v1/kodeverk")
                .header(HttpHeaders.AUTHORIZATION, "Basic $credentials")
                .content("foo"))
            .andExpect(status().isOk)
            .andExpect(content().json("""{ "response": "bar"}"""))
    }
}
