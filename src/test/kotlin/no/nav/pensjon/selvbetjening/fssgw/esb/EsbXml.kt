package no.nav.pensjon.selvbetjening.fssgw.esb

object EsbXml {

    val utbetalingResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentPeriodisertUtbetalingListeResponse xmlns:inf="http://nav-cons-pen-pselv-utbetaling/no/nav/inf">
            <hentPeriodisertUtbetalingListeResponse/>
        </inf:hentPeriodisertUtbetalingListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val pingResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header>
        <p:StelvioContext xsi:type="p:StelvioContext" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.nav.no/StelvioContextPropagation">
            <applicationId>App1</applicationId>
            <correlationId>Call1</correlationId>
            <userId>User1</userId>
        </p:StelvioContext>
    </soapenv:Header>
    <soapenv:Body>
        <inf:tpsGetApplicationVersionResponse xmlns:inf="http://nav-lib-test/no/nav/inf">
            <tpsGetApplicationVersionResponse>
                <status>OK</status>
            </tpsGetApplicationVersionResponse>
        </inf:tpsGetApplicationVersionResponse>
    </soapenv:Body>
</soapenv:Envelope>"""
}
