package no.nav.pensjon.selvbetjening.fssgw.esb

import no.nav.pensjon.selvbetjening.fssgw.common.CallIdGenerator
import no.nav.pensjon.selvbetjening.fssgw.common.ServiceClient
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.nio.charset.StandardCharsets

@WebMvcTest(EsbSelfTestController::class)
internal class EsbSelfTestControllerTest {

    private val pingPath = "/nav-cons-test-getapplicationversionWeb/sca/TESTGetApplicationVersionWSEXP"

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var serviceClient: ServiceClient

    @MockBean
    lateinit var callIdGenerator: CallIdGenerator

    @Test
    fun `ping request results in ping response XML`() {
        `when`(serviceClient.doPost(anyString(), anyMap(), anyString()))
            .thenReturn(EsbXml.pingResponseBody)
        val expectedMediaType = MediaType(MediaType.TEXT_XML, StandardCharsets.UTF_8)

        mvc.perform(
            post(pingPath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE)
                .content("""<?xml version="1.0" encoding="UTF-8"?><foo/>"""))
            .andExpect(status().isOk)
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, expectedMediaType.toString()))
            .andExpect(content().xml(EsbXml.pingResponseBody))
    }
}
