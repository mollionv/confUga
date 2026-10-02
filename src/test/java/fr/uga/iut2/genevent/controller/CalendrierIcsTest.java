package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.Batiment;
import fr.uga.iut2.genevent.modele.Conference;
import fr.uga.iut2.genevent.modele.Salle;
import fr.uga.iut2.genevent.modele.Session;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CalendrierIcsTest {

    private Salle salle() {
        return new Salle(1, 30, 0, 0, false, 10, 0);
    }

    private Session session(String nom, LocalDate date, LocalTime debut, LocalTime fin, java.util.List<String> intervenants, Salle salle, Conference conf) {
        return new Session(nom, date, debut, fin, new ArrayList<>(intervenants), salle, conf);
    }

    @Test
    void calendrierVideEncadre() {
        Conference conf = new Conference(1, "Vide", "d", "r");
        String ics = CalendrierIcs.exporter(conf);
        assertTrue(ics.startsWith("BEGIN:VCALENDAR"));
        assertTrue(ics.contains("END:VCALENDAR"));
        assertFalse(ics.contains("BEGIN:VEVENT"));
    }

    @Test
    void nomConferenceDansLeCalendrier() {
        Conference conf = new Conference(1, "MaConf", "d", "r");
        assertTrue(CalendrierIcs.exporter(conf).contains("X-WR-CALNAME:MaConf"));
    }

    @Test
    void unEvenementParSession() {
        Conference conf = new Conference(1, "C", "d", "r");
        conf.addSession(session("S1", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), Arrays.asList("Alice"), salle(), conf));
        conf.addSession(session("S2", LocalDate.of(2026, 6, 1), LocalTime.of(10, 0), LocalTime.of(11, 0), Arrays.asList("Bob"), salle(), conf));
        String ics = CalendrierIcs.exporter(conf);
        assertEquals(2, compter(ics, "BEGIN:VEVENT"));
        assertEquals(2, compter(ics, "END:VEVENT"));
    }

    @Test
    void datesAuFormatIcs() {
        Conference conf = new Conference(1, "C", "d", "r");
        conf.addSession(session("S1", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 30), Arrays.asList("Alice"), salle(), conf));
        String ics = CalendrierIcs.exporter(conf);
        assertTrue(ics.contains("DTSTART:20260601T090000"));
        assertTrue(ics.contains("DTEND:20260601T103000"));
    }

    @Test
    void echappementVirguleEtPointVirgule() {
        Conference conf = new Conference(1, "C", "d", "r");
        conf.addSession(session("Java; POO, suite", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), Arrays.asList("Alice"), salle(), conf));
        String ics = CalendrierIcs.exporter(conf);
        assertTrue(ics.contains("SUMMARY:Java\\; POO\\, suite"));
    }

    @Test
    void lieuContientEspaceEtBatiment() {
        Batiment batiment = new Batiment("IMAG", "rue", "Grenoble", 38000);
        Salle salle = salle();
        batiment.addEspace(salle);
        Conference conf = new Conference(1, "C", "d", "r");
        conf.addSession(session("S1", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), Arrays.asList("Alice"), salle, conf));
        String ics = CalendrierIcs.exporter(conf);
        assertTrue(ics.contains("IMAG"));
    }

    @Test
    void descriptionContientConferenceEtIntervenant() {
        Conference conf = new Conference(1, "GrandeConf", "d", "r");
        conf.addSession(session("S1", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), Arrays.asList("Alice"), salle(), conf));
        String ics = CalendrierIcs.exporter(conf).replace("\r\n ", "");
        assertTrue(ics.contains("Conference : GrandeConf"));
        assertTrue(ics.contains("Alice"));
    }

    @Test
    void sessionsTrieesChronologiquement() {
        Conference conf = new Conference(1, "C", "d", "r");
        conf.addSession(session("Tard", LocalDate.of(2026, 6, 2), LocalTime.of(9, 0), LocalTime.of(10, 0), Arrays.asList("Alice"), salle(), conf));
        conf.addSession(session("Tot", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), Arrays.asList("Bob"), salle(), conf));
        String ics = CalendrierIcs.exporter(conf);
        assertTrue(ics.indexOf("DTSTART:20260601") < ics.indexOf("DTSTART:20260602"));
    }

    @Test
    void lignesTermineesParCrlf() {
        Conference conf = new Conference(1, "C", "d", "r");
        assertTrue(CalendrierIcs.exporter(conf).contains("\r\n"));
    }

    private int compter(String texte, String motif) {
        int total = 0;
        int index = texte.indexOf(motif);
        while (index >= 0) {
            total++;
            index = texte.indexOf(motif, index + motif.length());
        }
        return total;
    }
}
