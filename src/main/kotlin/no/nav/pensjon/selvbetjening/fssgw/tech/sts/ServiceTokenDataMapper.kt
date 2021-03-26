package no.nav.pensjon.selvbetjening.fssgw.tech.sts

import java.time.LocalDateTime

object ServiceTokenDataMapper {

    fun from(dto: ServiceTokenDataDto, time: LocalDateTime): ServiceTokenData =
            ServiceTokenData(
                    dto.accessToken,
                    dto.tokenType,
                    time,
                    dto.expiresIn)
}
