package no.nav.pensjon.selvbetjening.fssgw.common

import java.util.*
import javax.servlet.http.HttpServletRequest

abstract class UnprotectedControllerBase(
    serviceClient: ServiceClient,
    egressEndpoint: String) : ControllerBase(serviceClient, egressEndpoint) {

    override fun checkIngressAuth(request: HttpServletRequest) {
        // No operation
    }

    override fun addAuthHeaderIfNeeded(request: HttpServletRequest, headers: TreeMap<String, String>) {
        // No operation
    }
}
