package no.nav.pensjon.selvbetjening.fssgw.esb

object EsbXml {

    val brukerprofilResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentBrukerprofilListeResponse xmlns:inf="http://nav-cons-pen-pselv-brukerprofil/no/nav/inf">
            <hentBrukerprofilListeResponse>
                <brukerprofiler>
                    <brukerprofilId>1</brukerprofilId>
                </brukerprofiler>
            </hentBrukerprofilListeResponse>
        </inf:hentBrukerprofilListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val fullmaktRequestBody: String
        get() = """<?xml version='1.0' encoding='UTF-8'?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header>
        <scp:StelvioContext xmlns="" xmlns:scp="http://www.nav.no/StelvioContextPropagation">
            <applicationId>PP01</applicationId>
            <correlationId>4dcbaf90-30f2-4b89-bdae-4cb22d418c2d</correlationId>
            <userId>PP01</userId>
        </scp:StelvioContext>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" soapenv:mustUnderstand="1">
            <wsse:UsernameToken>
                <wsse:Username>srvpselv</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">******</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soapenv:Header>
    <soapenv:Body>
        <inf:hentFullmaktListe xmlns:inf="http://nav-cons-pen-pselv-fullmakt/no/nav/inf">
            <hentFullmaktListeRequest>
                <fnr>01020312345</fnr>
                <hentDetaljer>false</hentDetaljer>
            </hentFullmaktListeRequest>
        </inf:hentFullmaktListe>
    </soapenv:Body>
</soapenv:Envelope>"""

    val fullmaktResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentFullmaktListeResponse xmlns:inf="http://nav-cons-pen-pselv-fullmakt/no/nav/inf">
            <hentFullmaktListeResponse>
                <fullmakter>
                    <fullmaktId>1</fullmaktId>
                    <kodefullmaktType>SELVBET</kodefullmaktType>
                    <kodefullmaktNiva>FULLSTENDIG</kodefullmaktNiva>
                    <opprettetAv>01020312345</opprettetAv>
                    <opprettetDato>2021-08-31</opprettetDato>
                    <endretAv>01020312345</endretAv>
                    <endretDato>2021-08-31</endretDato>
                    <fomDato>2021-08-31</fomDato>
                    <gyldig>true</gyldig>
                    <versjon>0</versjon>
                    <fagomrade>PEN</fagomrade>
                    <fullmaktsgiver>01020312345</fullmaktsgiver>
                    <fullmaktshaver>02030423456</fullmaktshaver>
                    <fullmaktsgiverKode>PERSON</fullmaktsgiverKode>
                    <fullmaktshaverKode>PERSON</fullmaktshaverKode>
                </fullmakter>
            </hentFullmaktListeResponse>
        </inf:hentFullmaktListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val henvendelseResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentHenvendelseListeResponse xmlns:inf="http://nav-cons-pen-pselv-henvendelse/no/nav/inf">
            <hentHenvendelseListeResponse>
                <henvendelser>
                    <henvendelseId>1</henvendelseId>
                </henvendelser>
            </hentHenvendelseListeResponse>
        </inf:hentHenvendelseListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val inntektResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentInntektListeResponse xmlns:inf="http://nav-cons-pen-pselv-inntekt/no/nav/inf">
            <hentInntektListeResponse>
                <inntekter>
                    <inntektId>1</inntektId>
                </inntekter>
            </hentInntektListeResponse>
        </inf:hentInntektListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

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
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">******</wsse:Password>
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

    val ppen004ResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentPpen004ListeResponse xmlns:inf="http://nav-cons-pen-pselv-ppen004/no/nav/inf">
            <hentPpen004ListeResponse>
                <ppen004er>
                    <ppen004Id>1</ppen004Id>
                </ppen004er>
            </hentPpen004ListeResponse>
        </inf:hentPpen004ListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val samhandlerResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentSamhandlerListeResponse xmlns:inf="http://nav-cons-pen-pselv-samhandler/no/nav/inf">
            <hentSamhandlerListeResponse>
                <samhandlerer>
                    <samhandlerId>1</samhandlerId>
                </samhandlerer>
            </hentSamhandlerListeResponse>
        </inf:hentSamhandlerListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val tjenestepensjonResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentTjenestepensjonListeResponse xmlns:inf="http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf">
            <hentTjenestepensjonListeResponse>
                <tjenestepensjoner>
                    <tjenestepensjonId>1</tjenestepensjonId>
                </tjenestepensjoner>
            </hentTjenestepensjonListeResponse>
        </inf:hentTjenestepensjonListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val utbetalingResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentUtbetalingListeResponse xmlns:inf="http://nav-cons-pen-pselv-utbetaling/no/nav/inf">
            <hentUtbetalingListeResponse>
                <utbetalinger>
                    <utbetalingId>1</utbetalingId>
                </utbetalinger>
            </hentUtbetalingListeResponse>
        </inf:hentUtbetalingListeResponse>
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
                    <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">******</wsse:Password>
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
