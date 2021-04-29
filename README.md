# pensjon-selvbetjening-fss-gateway

Gateway-/proxy-applikasjon for at pensjonsrelaterte applikasjoner i GCP skal kunne nå tjenester i FSS.

Teknologi: Java 11, Kotlin, Spring Boot og Maven.

Endepunkter:

- /api/pdl (Persondataløsningen)
- /api/pen (Pensjonsfaglig kjerne)

For å benytte endepunktene kreves *bearer*-token utstedt av TokenDings.

## Henvendelser

NAV-interne henvendelser kan sendes via Slack i kanalen [#po-pensjon-teamselvbetjening](https://nav-it.slack.com/archives/C014M7U1GBY).
