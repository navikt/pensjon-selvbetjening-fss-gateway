package no.nav.pensjon.selvbetjening.fssgw.esb

object EsbXml {

    val penPersonResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentPenPersonListeResponse xmlns:inf="http://nav-cons-pen-pselv-penPerson/no/nav/inf">
            <hentPenPersonListeResponse>
                <penPersoner>
                    <penPersonId>1</penPersonId>
                </penPersoner>
            </hentPenPersonListeResponse>
        </inf:hentPenPersonListeResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

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

    val pselvPersonResponseBody: String
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

    val journalbehandlingRequestBody: String
        get() = """<?xml version='1.0' encoding='UTF-8'?>
<S:Envelope xmlns:S="http://schemas.xmlsoap.org/soap/envelope/">
    <S:Header>
        <To xmlns="http://www.w3.org/2005/08/addressing">https://tjenestebuss-q2.adeo.no/nav-tjeneste-journalbehandling_v1Web/sca/JournalbehandlingWSEXP</To>
        <Action xmlns="http://www.w3.org/2005/08/addressing">http://nav.no/virksomhet/tjenester/arkiv/journalbehandling/v1/Journalbehandling/lagreDokumentOgOpprettJournalpostRequest</Action>
        <ReplyTo xmlns="http://www.w3.org/2005/08/addressing">
            <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>
        </ReplyTo>
        <FaultTo xmlns="http://www.w3.org/2005/08/addressing">
            <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>
        </FaultTo>
        <MessageID xmlns="http://www.w3.org/2005/08/addressing">uuid:44a6d212-766c-492a-9e8b-8c3b913fe716</MessageID>
        <scp:StelvioContext xmlns="" xmlns:scp="http://www.nav.no/StelvioContextPropagation">
            <applicationId>PP01</applicationId>
            <correlationId>4dcbaf90-30f2-4b89-bdae-4cb22d418c2d</correlationId>
            <userId>PP01</userId>
        </scp:StelvioContext>
        <wsse:Security xmlns:wsse="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd" S:mustUnderstand="1">
            <wsse:UsernameToken>
                <wsse:Username>********</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">********</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </S:Header>
    <S:Body>
        <ns2:lagreDokumentOgOpprettJournalpost xmlns:ns2="http://nav.no/virksomhet/tjenester/arkiv/journalbehandling/v1">
            <request>
                <saksrelasjon>
                    <fagsystemKode>PEN</fagsystemKode>
                    <feilregistrert>false</feilregistrert>
                    <sakId>22966681</sakId>
                    <endretAvNavn>Selvbetjening</endretAvNavn>
                </saksrelasjon>
                <journalpostDokumentInfoRelasjonListe>
                    <dokumentInfo>
                        <fildetaljerListe>
                            <fil>JVBERi0xLjQKJaqrrK0KMSAwIG9iago8PAovVGl0b...zCj4+CnN0YXJ0eHJlZgo1MzI1MAolJUVPRgo=</fil>
                            <filtypeKode>PDF</filtypeKode>
                            <variantFormatKode>ARKIV</variantFormatKode>
                        </fildetaljerListe>
                        <kategoriKode>ES</kategoriKode>
                        <tittel>Søknad om endring av alderspensjon</tittel>
                        <sensitivt>false</sensitivt>
                    </dokumentInfo>
                    <tilknyttetJournalpostSomKode>HOVEDDOKUMENT</tilknyttetJournalpostSomKode>
                    <tilknyttetAvNavn>Selvbetjening</tilknyttetAvNavn>
                </journalpostDokumentInfoRelasjonListe>
                <faktiskDistribusjonskanalKode>E</faktiskDistribusjonskanalKode>
                <gjelderListe>
                    <brukerId>01896097551</brukerId>
                    <brukertypeKode>PERSON</brukertypeKode>
                </gjelderListe>
                <mottattDato>2023-01-31T00:36:18.533+01:00</mottattDato>
                <innhold>Søknad om endring av alderspensjon</innhold>
                <journalForendeEnhetId>9999</journalForendeEnhetId>
                <journalposttypeKode>I</journalposttypeKode>
                <journalstatusKode>J</journalstatusKode>
                <dokumentDato>2023-01-31T00:36:18.533+01:00</dokumentDato>
                <fagomradeKode>PEN</fagomradeKode>
                <avsenderMottaker>Vertikal Brille</avsenderMottaker>
                <avsenderMottakerId>01896097551</avsenderMottakerId>
                <opprettetAvNavn>Selvbetjening</opprettetAvNavn>
            </request>
        </ns2:lagreDokumentOgOpprettJournalpost>
    </S:Body>
</S:Envelope>"""

    val journalbehandlingResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header xmlns:wsa="http://www.w3.org/2005/08/addressing">
        <wsa:Action>http://nav.no/virksomhet/tjenester/arkiv/journalbehandling/v1/Journalbehandling/lagreDokumentOgOpprettJournalpostResponse</wsa:Action>
        <wsa:RelatesTo>uuid:44a6d212-766c-492a-9e8b-8c3b913fe716</wsa:RelatesTo>
    </soapenv:Header>
    <soapenv:Body>
        <v1:lagreDokumentOgOpprettJournalpostResponse xmlns:v1="http://nav.no/virksomhet/tjenester/arkiv/journalbehandling/v1">
            <response>
                <journalpostId>453826525</journalpostId>
            </response>
        </v1:lagreDokumentOgOpprettJournalpostResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val tjenestepensjonRequestBody: String
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
                <wsse:Username>********</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">********</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soapenv:Header>
    <soapenv:Body>
        <inf:finnTjenestepensjonForhold xmlns:inf="http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf">
            <finnTjenestepensjonForholdRequest>
                <hentSamhandlerInfo>true</hentSamhandlerInfo>
                <fnr>65915200189</fnr>
            </finnTjenestepensjonForholdRequest>
        </inf:finnTjenestepensjonForhold>
    </soapenv:Body>
</soapenv:Envelope>"""

    val tjenestepensjonResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:finnTjenestepensjonForholdResponse xmlns:inf="http://nav-cons-pen-pselv-tjenestepensjon/no/nav/inf">
            <finnTjenestepensjonForholdResponse>
                <fnr>65915200189</fnr>
                <tjenestepensjonForholdene>
                    <forholdId>33587178</forholdId>
                    <tssEksternId>80001580761</tssEksternId>
                    <navn>Statens pensjonskasse</navn>
                    <tpNr>3010</tpNr>
                    <harUtlandPensjon>false</harUtlandPensjon>
                    <samtykkeSimuleringKode>N</samtykkeSimuleringKode>
                    <harSimulering>false</harSimulering>
                    <tjenestepensjonYtelseListe>
                        <ytelseId>33587180</ytelseId>
                        <innmeldtFom>2014-04-01</innmeldtFom>
                        <ytelseKode>ALDER</ytelseKode>
                        <ytelseBeskrivelse>ALDER</ytelseBeskrivelse>
                        <iverksattFom>2022-09-20</iverksattFom>
                    </tjenestepensjonYtelseListe>
                    <endringsInfo>
                        <endretAvId>srvtest</endretAvId>
                        <opprettetAvId>srvtest</opprettetAvId>
                        <endretDato>2022-10-20</endretDato>
                        <opprettetDato>2022-10-20</opprettetDato>
                        <kildeId>PP01</kildeId>
                    </endringsInfo>
                    <avdelingType>TPOF</avdelingType>
                </tjenestepensjonForholdene>
                <endringsInfo>
                    <endretAvId>UNKNOWN</endretAvId>
                    <opprettetAvId>UNKNOWN</opprettetAvId>
                    <endretDato>2022-04-20</endretDato>
                    <opprettetDato>2022-04-20</opprettetDato>
                </endringsInfo>
            </finnTjenestepensjonForholdResponse>
        </inf:finnTjenestepensjonForholdResponse>
    </soapenv:Body>
</soapenv:Envelope>"""

    val utbetalingRequestBody: String
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
                <wsse:Username>********</wsse:Username>
                <wsse:Password Type="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText">********</wsse:Password>
            </wsse:UsernameToken>
        </wsse:Security>
    </soapenv:Header>
    <soapenv:Body>
        <inf:hentPeriodisertUtbetalingListe xmlns:inf="http://nav-cons-pen-pselv-utbetaling/no/nav/inf">
            <hentPeriodisertUtbetalingListeRequest>
                <fnrOrgnr>65915200189</fnrOrgnr>
                <fomDato>2022-10-01+02:00</fomDato>
                <tomDato>2023-01-19+01:00</tomDato>
            </hentPeriodisertUtbetalingListeRequest>
        </inf:hentPeriodisertUtbetalingListe>
    </soapenv:Body>
</soapenv:Envelope>"""

    val utbetalingResponseBody: String
        get() = """<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Body>
        <inf:hentPeriodisertUtbetalingListeResponse xmlns:inf="http://nav-cons-pen-pselv-utbetaling/no/nav/inf">
            <hentPeriodisertUtbetalingListeResponse/>
        </inf:hentPeriodisertUtbetalingListeResponse>
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
