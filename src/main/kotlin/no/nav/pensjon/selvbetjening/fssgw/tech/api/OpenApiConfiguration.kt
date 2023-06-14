package no.nav.pensjon.selvbetjening.fssgw.tech.api

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration of OpenAPI (formerly known as Swagger).
 */
@Configuration
class OpenApiConfiguration {

    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Pensjon FSS gateway API")
                    .description("Gateway til Fagsystemsonen for pensjonsapplikasjoner")
                    .version("v1.0.0")
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Confluence-dokumentasjon for Pensjon FSS gateway")
                    .url("https://confluence.adeo.no/display/PEN/FSS-gateway-app")
            )
    }

    @Bean
    fun penGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("PEN")
            .pathsToMatch("/pen/**")
            .build()
    }

    @Bean
    fun webServicesSupportGroup(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("Web Services support")
            .pathsToMatch("/ws-support/**")
            .build()
    }
}
