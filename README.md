# pensjon-selvbetjening-fss-gateway

Gateway-/proxy-applikasjon for at pensjonsrelaterte applikasjoner i allmenn sky (GCP) skal kunne nå tjenester i Fagsystemsonen (FSS).

Teknologi: Java 17, Kotlin, Spring Boot og Maven.

Støttede tjenester:

- DKIF (Digital kontaktinformasjon)
- EREG (Enhetsregisteret)
- ESB (Enterprise Service Bus, tjenestebuss)
- Dok-arkiv (JOARK)
- Inntektskomponenten
- Kodeverk
- PDL (Persondataløsningen)
- PEN (Pensjonsfaglig kjerne)
- POPP (Pensjonsfaglig opptjeningsregister)
- TP (Tjenestepensjon)
- TSS (Tjenestebasert samhandlersystem)

For å benytte endepunktene kreves *bearer*-token (TokenX, Azure AD On-Behalf-Of, eller Client credentials).

## Øvrig dokumentasjon

NAV-intern dokumentasjon (Confluence): [FSS-gateway-app](https://confluence.adeo.no/display/PEN/FSS-gateway-app)

## Henvendelser

NAV-interne henvendelser kan sendes via Slack i kanalen [#po-pensjon-teamselvbetjening](https://nav-it.slack.com/archives/C014M7U1GBY).
