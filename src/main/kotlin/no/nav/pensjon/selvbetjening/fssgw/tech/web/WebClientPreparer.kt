package no.nav.pensjon.selvbetjening.fssgw.tech.web

import org.springframework.web.reactive.function.client.WebClient

object WebClientPreparer {

    fun webClient(requiresProxy: Boolean, proxyUri: String) =
            if (requiresProxy) proxyAwareWebClient(proxyUri)
            else WebClient.create()

    private fun proxyAwareWebClient(proxyUri: String) =
            WebClient.builder()
                    .clientConnector(WebClientProxyConfig.clientHttpConnector(proxyUri))
                    .build()
}
