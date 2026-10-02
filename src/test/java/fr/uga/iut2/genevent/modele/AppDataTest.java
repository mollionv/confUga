package fr.uga.iut2.genevent.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppDataTest {

    private AppData appData;
    private Salle salle;
    private final LocalDate jour = LocalDate.of(2026, 6, 1);

    @BeforeEach
    void setUp() {
        appData = new AppData();
        salle = new Salle(1, 30, 0, 0, false, 10, 0);
    }

    private Session session(LocalTime debut, LocalTime fin, EspaceReunion espace, Conference conf) {
        Session s = new Session("S", jour, debut, fin, new ArrayList<>(), espace, conf);
        conf.addSession(s);
        return s;
    }

    @Test
    void numerotationIncrementaleDesConferences() {
        Conference c1 = new Conference(0, "A", "d", "r");
        Conference c2 = new Conference(0, "B", "d", "r");
        appData.ajouterConference(c1);
        appData.ajouterConference(c2);
        assertEquals(1, c1.getNumConference());
        assertEquals(2, c2.getNumConference());
    }

    @Test
    void numerotationUtiliseLeMaxExistant() {
        Conference c1 = new Conference(0, "A", "d", "r");
        appData.ajouterConference(c1);
        c1.setNumConference(10);
        Conference c2 = new Conference(0, "B", "d", "r");
        appData.ajouterConference(c2);
        assertEquals(11, c2.getNumConference());
    }

    @Test
    void suppressionConference() {
        Conference c1 = new Conference(0, "A", "d", "r");
        appData.ajouterConference(c1);
        appData.supprimerConference(c1);
        assertTrue(appData.getConferences().isEmpty());
    }

    @Test
    void ajoutBatimentRetourneEtEnregistre() {
        Batiment batiment = appData.ajouterBatiment("IMAG", "rue", "Grenoble", 38000);
        assertNotNull(batiment);
        assertTrue(appData.getBatiments().contains(batiment));
    }

    @Test
    void suppressionBatiment() {
        Batiment batiment = appData.ajouterBatiment("IMAG", "rue", "Grenoble", 38000);
        appData.supprimerBatiment(batiment);
        assertTrue(appData.getBatiments().isEmpty());
    }

    @Test
    void conflitDetecteSurChevauchement() {
        Conference conf = new Conference(1, "C", "d", "r");
        appData.ajouterConference(conf);
        session(LocalTime.of(9, 0), LocalTime.of(11, 0), salle, conf);
        List<Session> conflits = appData.sessionsEnConflit(salle, jour, LocalTime.of(10, 0), LocalTime.of(12, 0), null, conf);
        assertEquals(1, conflits.size());
    }

    @Test
    void pasDeConflitSiCreneauxAdjacents() {
        Conference conf = new Conference(1, "C", "d", "r");
        appData.ajouterConference(conf);
        session(LocalTime.of(9, 0), LocalTime.of(10, 0), salle, conf);
        assertTrue(appData.estDisponible(salle, jour, LocalTime.of(10, 0), LocalTime.of(11, 0), null, conf));
    }

    @Test
    void pasDeConflitSurEspaceDifferent() {
        Conference conf = new Conference(1, "C", "d", "r");
        appData.ajouterConference(conf);
        session(LocalTime.of(9, 0), LocalTime.of(11, 0), salle, conf);
        Salle autre = new Salle(2, 30, 0, 0, false, 10, 0);
        assertTrue(appData.estDisponible(autre, jour, LocalTime.of(9, 0), LocalTime.of(11, 0), null, conf));
    }

    @Test
    void pasDeConflitSurDateDifferente() {
        Conference conf = new Conference(1, "C", "d", "r");
        appData.ajouterConference(conf);
        session(LocalTime.of(9, 0), LocalTime.of(11, 0), salle, conf);
        assertTrue(appData.estDisponible(salle, LocalDate.of(2026, 6, 2), LocalTime.of(9, 0), LocalTime.of(11, 0), null, conf));
    }

    @Test
    void sessionExclueIgnoreeDesConflits() {
        Conference conf = new Conference(1, "C", "d", "r");
        appData.ajouterConference(conf);
        Session s = session(LocalTime.of(9, 0), LocalTime.of(11, 0), salle, conf);
        assertTrue(appData.estDisponible(salle, jour, LocalTime.of(9, 0), LocalTime.of(11, 0), s, conf));
    }

    @Test
    void conferenceEnCoursVerifieeMemeSiNonEnregistree() {
        Conference conf = new Conference(1, "C", "d", "r");
        session(LocalTime.of(9, 0), LocalTime.of(11, 0), salle, conf);
        List<Session> conflits = appData.sessionsEnConflit(salle, jour, LocalTime.of(10, 0), LocalTime.of(12, 0), null, conf);
        assertEquals(1, conflits.size());
    }

    @Test
    void aucunConflitAvecParametresNuls() {
        assertTrue(appData.sessionsEnConflit(null, jour, LocalTime.of(9, 0), LocalTime.of(10, 0), null, null).isEmpty());
    }
}
