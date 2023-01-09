package no.nav.pensjon.selvbetjening.fssgw.tech.web

import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

object WebClientPreparer {

    private const val MAX_IN_MEMORY_SIZE = 10 * 1024 * 1024 // 10 MB

    fun webClient(requiresProxy: Boolean, proxyUri: String) =
        if (requiresProxy) proxyAwareWebClient(proxyUri)
        else WebClient.create()

    fun largeBufferWebClient(): WebClient {
        val httpClient = HttpClient.create().wiretap(true)

        val strategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
            .build()

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies).build()
    }

    private fun proxyAwareWebClient(proxyUri: String) =
        WebClient.builder()
            .clientConnector(WebClientProxyConfig.clientHttpConnector(proxyUri))
            .build()
}
