package no.nav.pensjon.selvbetjening.fssgw.common

import java.util.*
import javax.servlet.http.HttpServletRequest

abstract class UnprotectedControllerBase(
    serviceClient: ServiceClient,
    callIdGenerator: CallIdGenerator,
    egressEndpoint: String)
    : ControllerBase(serviceClient, callIdGenerator, egressEndpoint) {

    override fun checkIngressAuth(request: HttpServletRequest) {
        // No operation
    }

    override fun provideBodyAuth(body: String) = body

    override fun provideHeaderAuth(request: HttpServletRequest, headers: TreeMap<String, String>) {
        // No operation
    }
}
