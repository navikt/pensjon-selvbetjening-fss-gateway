package no.nav.pensjon.selvbetjening.fssgw.tech.web

import org.slf4j.LoggerFactory
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.ProxyProvider
import reactor.netty.transport.ProxyProvider.TypeSpec

object WebClientProxyConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    fun clientHttpConnector(proxyUri: String): ReactorClientHttpConnector {
        val uri = UriUtil.uriFrom(proxyUri)
        log.info("URI: Host: '{}'. Port: {}.", uri.host, uri.port)

        val httpClient = HttpClient
                .create()
                .proxy { p -> proxySpec(p, uri.host, uri.port) }

        return ReactorClientHttpConnector(httpClient)
    }

    private fun proxySpec(proxy: TypeSpec, host: String, port: Int): ProxyProvider.Builder {
        return proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host(host)
                .port(port)
    }
}
