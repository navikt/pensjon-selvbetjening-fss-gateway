# pensjon-selvbetjening-fss-gateway

Gateway-/proxy-applikasjon for at pensjonsrelaterte applikasjoner i allmenn sky (GCP) skal kunne nå tjenester i Fagsystemsonen (FSS).

Støttede tjenester:

- ESB (Enterprise Service Bus, tjenestebuss)
- JOARK
- Gandalf STS
- Norsk Pensjon (via partner-gateway)
- Pensjon-regler
- POPP (Pensjonsfaglig opptjeningsregister)
- Sporingslogging for pensjon
- Tjenestepensjonsordninger (via partner-gateway)
- TP (Tjenestepensjon)

For å benytte endepunktene kreves *bearer*-token (TokenX, Azure AD On-Behalf-Of, eller Client credentials).

## API

Se [Swagger/OpenAPI-dokumentasjon](https://pensjon-selvbetjening-fss-gateway.dev.intern.nav.no/swagger-ui/index.html)

## Teknologi

* [Java 17](https://openjdk.org/projects/jdk/17/)
* [Kotlin](https://kotlinlang.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)

## Øvrig dokumentasjon

Nav-intern dokumentasjon (Confluence): [FSS-gateway-app](https://confluence.adeo.no/display/PEN/FSS-gateway-app)

## Henvendelser

Nav-interne henvendelser kan sendes via Slack i kanalen [#pensjonskalkulator](https://nav-it.slack.com/archives/C04M46SPSRL).
