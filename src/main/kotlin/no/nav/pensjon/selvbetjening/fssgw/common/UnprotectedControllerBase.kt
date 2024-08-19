package no.nav.pensjon.selvbetjening.fssgw.common

import java.util.*
import jakarta.servlet.http.HttpServletRequest

abstract class UnprotectedControllerBase(
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String) : ControllerBase(
    serviceClient, callIdGenerator, egressEndpoint
) {
    override fun checkIngressAuth(request: HttpServletRequest): String = "none"

    override fun provideBodyAuth(body: String) = body

    override fun provideHeaderAuth(
        request: HttpServletRequest,
        headers: TreeMap<String, String>,
        serviceUserId: Int
    ) {
        // No operation
    }
}
