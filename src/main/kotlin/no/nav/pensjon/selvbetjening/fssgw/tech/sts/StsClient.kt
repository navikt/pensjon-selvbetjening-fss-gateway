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
    @Value("\${fg.sts.selfservice.username}") private val serviceUsername1: String,
    @Value("\${fg.sts.selfservice.password}") private val servicePassword1: String,
    @Value("\${fg.sts.general.username}") private val serviceUsername2: String,
    @Value("\${fg.sts.general.password}") private val servicePassword2: String,
    @Value("\${fg.sts.tp.username}") private val serviceUsername3: String,
    @Value("\${fg.sts.tp.password}") private val servicePassword3: String
) : ServiceTokenGetter {

    private val webClient: WebClient = WebClient.create()
    private val log = LoggerFactory.getLogger(javaClass)
    private var tokenData1: ServiceTokenData? = null
    private var tokenData2: ServiceTokenData? = null
    private var tokenData3: ServiceTokenData? = null

    private val serviceUserCredentials: Map<Int, String> =
        mapOf(
            1 to "$serviceUsername1:$servicePassword1",
            2 to "$serviceUsername2:$servicePassword2",
            3 to "$serviceUsername3:$servicePassword3"
        )

    override fun getServiceUserToken(serviceUserId: Int): ServiceTokenData =
        when (serviceUserId) {
            1 -> if (isCachedTokenValid(tokenData1)) tokenData1!!
            else freshTokenData(serviceUserId).also { tokenData1 = it }

            2 -> if (isCachedTokenValid(tokenData2)) tokenData2!!
            else freshTokenData(serviceUserId).also { tokenData2 = it }

            3 -> if (isCachedTokenValid(tokenData3)) tokenData3!!
            else freshTokenData(serviceUserId).also { tokenData3 = it }

            else -> throw IllegalArgumentException("Invalid serviceUserId: $serviceUserId")
        }

    private fun freshTokenData(serviceUserId: Int): ServiceTokenData {
        log.debug("Retrieving new token for service user")

        val uri = UriComponentsBuilder
            .fromHttpUrl("$baseUrl/$TOKEN_PATH")
            .queryParam(Oauth2ParamNames.GRANT_TYPE, GRANT_TYPE)
            .queryParam(Oauth2ParamNames.SCOPE, SCOPE)
            .toUriString()

        return try {
            val data: ServiceTokenDataDto = webClient
                .get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, authHeader(serviceUserId))
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

    private fun authHeader(serviceUserId: Int): String = "$AUTH_TYPE ${encodedCredentials(serviceUserId)}"

    private fun encodedCredentials(serviceUserId: Int): String {
        log.debug("---Servicebruker---: ${serviceUserCredentials[serviceUserId]}")          //FIXME:Remove
        return serviceUserCredentials[serviceUserId]?.let(::base64Encode)
            ?: throw IllegalArgumentException("Invalid serviceUserId: $serviceUserId")
    }

    private companion object {
        private const val TOKEN_PATH = "rest/v1/sts/token"
        private const val AUTH_TYPE = "Basic"
        private const val GRANT_TYPE = "client_credentials"
        private const val SCOPE = "openid"

        private fun base64Encode(value: String): String =
            Base64.getEncoder().encodeToString(value.toByteArray())
    }
}
