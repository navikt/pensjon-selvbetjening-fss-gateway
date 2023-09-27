package no.nav.pensjon.selvbetjening.fssgw.tech.web

import no.nav.pensjon.selvbetjening.fssgw.common.EgressException
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

object WebClientPreparer {

    private const val MAX_IN_MEMORY_SIZE = 10 * 1024 * 1024 // 10 MB

    fun webClient(requiresProxy: Boolean, proxyUri: String): WebClient =
        if (requiresProxy) proxyAwareWebClient(proxyUri)
        else WebClient.create()

    fun largeBufferWebClient(): WebClient {
        val httpClient = HttpClient.create()

        val strategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE) }
            .build()

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(strategies)
            .filter(ExchangeFilterFunction.ofResponseProcessor(::exchangeFilterResponseProcessor))
            .build()
    }

    private fun exchangeFilterResponseProcessor(response: ClientResponse): Mono<ClientResponse> {
        val status: HttpStatusCode = response.statusCode()

        return if (status.is4xxClientError || status.is5xxServerError) {
            response.bodyToMono(String::class.java)
                .flatMap { Mono.error(EgressException(it ?: "(no body)", status)) }
        } else {
            Mono.just(response)
        }
    }

    private fun proxyAwareWebClient(proxyUri: String) =
        WebClient.builder()
            .clientConnector(WebClientProxyConfig.clientHttpConnector(proxyUri))
            .build()
}
