package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2ParamNames
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Component
class StsClient(
    private val expirationChecker: ExpirationChecker,
    @Value("\${sts.url}") private val baseUrl: String,
    @Value("\${sts.username}") private val serviceUsername: String,
    @Value("\${sts.password}") private val servicePassword: String,
    @Value("\${sts.username2}") private val serviceUsername2: String,
    @Value("\${sts.password2}") private val servicePassword2: String
) : ServiceTokenGetter {

    private val webClient: WebClient = WebClient.create()
    private val log = LoggerFactory.getLogger(javaClass)
    private var tokenData: ServiceTokenData? = null
    private var tokenData2: ServiceTokenData? = null

    override fun getServiceUserToken(useServiceUser2: Boolean): ServiceTokenData =
        if (useServiceUser2) {
            if (isCachedTokenValid(tokenData2))
                tokenData2!!
            else
                freshTokenData(useServiceUser2 = true).also { tokenData2 = it }
        } else {
            if (isCachedTokenValid(tokenData))
                tokenData!!
            else
                freshTokenData(useServiceUser2 = false).also { tokenData = it }
        }

    private fun freshTokenData(useServiceUser2: Boolean): ServiceTokenData {
        log.debug("Retrieving new token for service user")
        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$baseUrl/$TOKEN_PATH")
            .queryParam(Oauth2ParamNames.GRANT_TYPE, GRANT_TYPE)
            .queryParam(Oauth2ParamNames.SCOPE, SCOPE)
        val uri = uriBuilder.toUriString()

        return try {
            val data: ServiceTokenDataDto = webClient
                .get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader(useServiceUser2))
                .retrieve()
                .bodyToMono(ServiceTokenDataDto::class.java)
                .block()
                ?: throw StsException("No token data in STS response")

            ServiceTokenDataMapper.from(data, expirationChecker.time())
        } catch (e: WebClientResponseException) {
            val message =
                "Failed to acquire service user token from $uri: ${e.message} | Response: ${e.responseBodyAsString}"
            log.error(message, e)
            throw StsException(message, e)
        } catch (e: RuntimeException) { // e.g. when connection broken
            val message = "Failed to access STS $uri: ${e.message}"
            log.error(message, e)
            throw StsException(message, e)
        }
    }

    private fun isCachedTokenValid(cachedValue: ServiceTokenData?): Boolean =
        cachedValue?.let { !expirationChecker.isExpired(it.issuedTime, it.expiresInSeconds) } ?: false

    private fun authHeader(useServiceUser2: Boolean): String = "$AUTH_TYPE ${credentials(useServiceUser2)}"

    private fun credentials(useServiceUser2: Boolean): String =
        Base64.getEncoder().encodeToString(plaintextCredentials(useServiceUser2).toByteArray())

    private fun plaintextCredentials(useServiceUser2: Boolean) =
        if (useServiceUser2)
            "$serviceUsername2:$servicePassword2"
        else
            "$serviceUsername:$servicePassword"

    private companion object {
        private const val TOKEN_PATH = "rest/v1/sts/token"
        private const val AUTH_TYPE = "Basic"
        private const val GRANT_TYPE = "client_credentials"
        private const val SCOPE = "openid"
    }
}
