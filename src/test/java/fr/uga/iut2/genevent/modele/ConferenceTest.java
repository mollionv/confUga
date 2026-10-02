package fr.uga.iut2.genevent.modele;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ConferenceTest {

    private Salle salle(int tarif) {
        return new Salle(1, 30, 0, 0, false, tarif, 0);
    }

    private Session session(String nom, LocalDate date, LocalTime debut, LocalTime fin, EspaceReunion espace) {
        return new Session(nom, date, debut, fin, new ArrayList<>(Arrays.asList("Alice")), espace, null);
    }

    @Test
    void ajoutEtRetraitSession() {
        Conference conf = new Conference(1, "Conf", "desc", "Paul");
        Session s = session("S1", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10));
        conf.addSession(s);
        assertEquals(1, conf.getSessions().size());
        conf.removeSession(s);
        assertTrue(conf.getSessions().isEmpty());
    }

    @Test
    void ajoutListeDeSessions() {
        Conference conf = new Conference(1, "Conf", "desc", "Paul");
        ArrayList<Session> liste = new ArrayList<>(Arrays.asList(
                session("S1", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)),
                session("S2", LocalDate.of(2026, 6, 1), LocalTime.of(10, 0), LocalTime.of(11, 0), salle(10))
        ));
        conf.addListSession(liste);
        assertEquals(2, conf.getSessions().size());
    }

    @Test
    void prixEstLaSommeDesSessions() {
        Conference conf = new Conference(1, "Conf", "desc", "Paul");
        conf.addSession(session("S1", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(11, 0), salle(10)));
        conf.addSession(session("S2", LocalDate.of(2026, 6, 1), LocalTime.of(11, 0), LocalTime.of(12, 0), salle(20)));
        assertEquals(40.0, conf.calculerPrix());
    }

    @Test
    void debutEtFinNullsSansSession() {
        Conference conf = new Conference(1, "Conf", "desc", "Paul");
        assertNull(conf.getDebut());
        assertNull(conf.getFin());
    }

    @Test
    void debutEstLaDateLaPlusTot() {
        Conference conf = new Conference(1, "Conf", "desc", "Paul");
        conf.addSession(session("S1", LocalDate.of(2026, 6, 3), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)));
        conf.addSession(session("S2", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)));
        conf.addSession(session("S3", LocalDate.of(2026, 6, 5), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)));
        assertEquals(LocalDate.of(2026, 6, 1), conf.getDebut());
    }

    @Test
    void finEstLaDateLaPlusTard() {
        Conference conf = new Conference(1, "Conf", "desc", "Paul");
        conf.addSession(session("S1", LocalDate.of(2026, 6, 3), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)));
        conf.addSession(session("S2", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)));
        conf.addSession(session("S3", LocalDate.of(2026, 6, 5), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)));
        assertEquals(LocalDate.of(2026, 6, 5), conf.getFin());
    }

    @Test
    void programmeContientNomEtIntervenant() {
        Conference conf = new Conference(1, "Conf", "desc", "Paul");
        conf.addSession(session("Maths", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), salle(10)));
        String programme = conf.getProgramme();
        assertTrue(programme.contains("Maths"));
        assertTrue(programme.contains("Alice"));
    }
}
