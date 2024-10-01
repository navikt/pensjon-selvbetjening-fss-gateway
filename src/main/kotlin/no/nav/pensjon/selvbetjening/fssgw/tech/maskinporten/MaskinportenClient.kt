package no.nav.pensjon.selvbetjening.fssgw.tech.maskinporten

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.util.*
import java.util.concurrent.TimeUnit

@Component
class MaskinportenClient(
    @Qualifier("maskinportenWebClient") private val webClient: WebClient,
    @Value("\${maskinporten.client-id}") val clientId: String,
    @Value("\${maskinporten.client-jwk}") val clientJwk: String,
    @Value("\${maskinporten.issuer}") val issuer: String,
    @Value("\${maskinporten.token-endpoint-url}") val endpoint: String,
) : MaskinportenTokenGetter {
    private val log = KotlinLogging.logger {}
    private val tokenCache: LoadingCache<String, MaskinportenToken> = Caffeine.newBuilder()
        .expireAfterWrite(EXPIRE_AFTER, EXPIRE_AFTER_TIME_UNITS)
        .build { k: String -> fetchToken(k) }

    override fun getToken(scope: String): MaskinportenToken {
        return tokenCache.get(scope)
    }

    fun fetchToken(scope: String): MaskinportenToken {
        val rsaKey = RSAKey.parse(clientJwk)
        val signedJWT = SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.keyID)
                .type(JOSEObjectType.JWT)
                .build(),
            JWTClaimsSet.Builder()
                .audience(issuer)
                .issuer(clientId)
                .claim("scope", scope)
                .issueTime(Date())
                .expirationTime(twoMinutesFromNow())
                .build()
        )
        signedJWT.sign(RSASSASigner(rsaKey.toRSAPrivateKey()))
        val response = webClient.post().uri(endpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .header("Accept", "*/*")
            .body(
                BodyInserters
                    .fromFormData("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                    .with("assertion", signedJWT.serialize())
            )
            .retrieve()
            .bodyToMono(MaskinportenToken::class.java)
            .block()
        log.info { "Hentet token fra maskinporten med scope(s): ${scope}" }
        return response ?: throw RuntimeException("Failed to get token from Maskinporten")
    }

    fun twoMinutesFromNow(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = Date();
        calendar.add(Calendar.SECOND, REQUEST_TOKEN_TO_EXPIRE_AFTER_SECONDS)

        return calendar.time
    }

    companion object {
        private val EXPIRE_AFTER_TIME_UNITS = TimeUnit.SECONDS
        private const val EXPIRE_AFTER: Long = 100
        private const val REQUEST_TOKEN_TO_EXPIRE_AFTER_SECONDS: Int = (EXPIRE_AFTER + 20).toInt()
    }
}