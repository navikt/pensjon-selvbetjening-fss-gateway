package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2ParamNames.GRANT_TYPE
import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2ParamNames.SCOPE
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Component
class StsConsumer(private val expirationChecker: ExpirationChecker,
                  @Value("\${sts.url}") private val baseUrl: String,
                  @Value("\${sts.username}") private val serviceUsername: String,
                  @Value("\${sts.password}") private val servicePassword: String) : ServiceTokenGetter {

    private val tokenPath = "rest/v1/sts/token"
    private val authType = "Basic"
    private val grantType = "client_credentials"
    private val scope = "openid"
    private val webClient: WebClient = WebClient.create()
    private val log = LoggerFactory.getLogger(javaClass)
    private var tokenData: ServiceTokenData? = null

    override fun getServiceUserToken(): ServiceTokenData =
            if (isCachedTokenValid) tokenData!! else freshTokenData.also { tokenData = it }

    private val freshTokenData: ServiceTokenData
        get() {
            log.debug("Retrieving new token for service user")
            val uriBuilder = UriComponentsBuilder.fromHttpUrl("$baseUrl/$tokenPath")
                    .queryParam(GRANT_TYPE, grantType)
                    .queryParam(SCOPE, scope)
            val uri = uriBuilder.toUriString()

            return try {
                val data: ServiceTokenDataDto = webClient
                        .get()
                        .uri(uri)
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .retrieve()
                        .bodyToMono(ServiceTokenDataDto::class.java)
                        .block()
                        ?: throw StsException("No token data in STS response")

                ServiceTokenDataMapper.from(data, expirationChecker.time())
            } catch (e: WebClientResponseException) {
                val message = "Failed to acquire service user token from $uri: ${e.message} | Response: ${e.responseBodyAsString}"
                log.error(message, e)
                throw StsException(message, e)
            } catch (e: RuntimeException) { // e.g. when connection broken
                val message = "Failed to access STS $uri: ${e.message}"
                log.error(message, e)
                throw StsException(message, e)
            }
        }

    private val isCachedTokenValid: Boolean
        get() = tokenData?.let { !expirationChecker.isExpired(it.issuedTime, it.expiresInSeconds) } ?: false

    private val authHeader: String
        get() = authType + " " + Base64.getEncoder().encodeToString("$serviceUsername:$servicePassword".toByteArray())
}
