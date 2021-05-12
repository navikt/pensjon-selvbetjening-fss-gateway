package no.nav.pensjon.selvbetjening.fssgw.tech.oauth2

import no.nav.pensjon.selvbetjening.fssgw.tech.jwt.KeyDataGetter

data class Oauth2Handler(
        val configGetter: Oauth2ConfigGetter,
        val keyDataGetter: KeyDataGetter,
        val acceptedAudience: String
)
