package no.nav.pensjon.selvbetjening.fssgw.tps

object TpsXml {

    val personRequestBody: String
        get() = """<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header>
        <scp:StelvioContext xmlns="" xmlns:scp="http://www.nav.no/StelvioContextPropagation">
            <applicationId>App1</applicationId>
            <correlationId>Call1</correlationId>
            <userId>User1</userId>
        </scp:StelvioContext>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
            <wsse:UsernameToken>
                <wsse:Username>user2</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">password1</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soapenv:Header>
    <soapenv:Body>
        <inf:hentPerson xmlns:inf="http://nav-cons-pen-pselv-person/no/nav/inf">
            <hentPersonRequest>
                <person>
                    <fodselsnummer>01020312345</fodselsnummer>
                </person>
                <hentEgenAnsatt>true</hentEgenAnsatt>
            </hentPersonRequest>
        </inf:hentPerson>
    </soapenv:Body>
</soapenv:Envelope>"""

    val personResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
        <soapenv:Body>
            <inf:hentPersonResponse xmlns:inf="http://nav-cons-pen-pselv-person/no/nav/inf">
                <hentPersonResponse>
                    <fodselsnummer>01020312345</fodselsnummer>
                    <kortnavn>MARVE FLEKSNES</kortnavn>
                </hentPersonResponse>
            </inf:hentPersonResponse>
        </soapenv:Body>
    </soapenv:Envelope>"""

    val pingRequestBody: String
        get() = """<?xml version='1.0' encoding='UTF-8'?>
    <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
        <soapenv:Header>
            <scp:StelvioContext xmlns="" xmlns:scp="http://www.nav.no/StelvioContextPropagation">
                <applicationId>App1</applicationId>
                <correlationId>Call1</correlationId>
                <userId>User1</userId>
            </scp:StelvioContext>
            <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
                <wsse:UsernameToken>
                    <wsse:Username>user2</wsse:Username>
                    <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">password1</wsse:Password>
                </wsse:UsernameToken>
            </wsse:Security>
        </soapenv:Header>
        <soapenv:Body>
            <inf:tpsGetApplicationVersion xmlns:inf="http://nav-lib-test/no/nav/inf">
                <tpsGetApplicationVersionRequest/>
            </inf:tpsGetApplicationVersion>
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
