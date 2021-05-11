package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.bean

import no.nav.pensjon.selvbetjening.fssgw.tech.oauth2.Oauth2BasicData
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Oauth2BeanConfig {

    @Bean
    @Qualifier("\${external-user}")
    fun externalUserOauth2BasicData(
            @Value("\${external-user.oauth2.well-known-url}") wellKnownUrl: String,
            @Value("\${external-user.oauth2.audience}") acceptedAudience: String)
            : Oauth2BasicData {
        return Oauth2BasicData(wellKnownUrl, acceptedAudience)
    }

    @Bean
    @Qualifier("\${internal-user}")
    fun internalUserOauth2BasicData(
            @Value("\${internal-user.oauth2.well-known-url}") wellKnownUrl: String,
            @Value("\${internal-user.oauth2.audience}") acceptedAudience: String)
            : Oauth2BasicData {
        return Oauth2BasicData(wellKnownUrl, acceptedAudience)
    }
}
