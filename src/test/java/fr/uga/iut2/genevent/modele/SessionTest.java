package fr.uga.iut2.genevent.modele;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SessionTest {

    private Salle salle(int tarif) {
        return new Salle(1, 30, 1, 0, false, tarif, 0);
    }

    private Session session(LocalTime debut, LocalTime fin, EspaceReunion espace) {
        return new Session("Cours", LocalDate.of(2026, 6, 1), debut, fin,
                new ArrayList<>(Arrays.asList("Alice", "Bob")), espace, null);
    }

    @Test
    void dureeDeuxHeures() {
        Session s = session(LocalTime.of(9, 0), LocalTime.of(11, 0), salle(10));
        assertEquals(2.0, s.getDureeHeures());
    }

    @Test
    void dureeAvecMinutes() {
        Session s = session(LocalTime.of(9, 0), LocalTime.of(10, 30), salle(10));
        assertEquals(1.5, s.getDureeHeures());
    }

    @Test
    void dureeNulleSiHeureManquante() {
        Session s = session(null, null, salle(10));
        assertEquals(0.0, s.getDureeHeures());
    }

    @Test
    void prixEstDureeFoisTarif() {
        Session s = session(LocalTime.of(9, 0), LocalTime.of(11, 0), salle(20));
        assertEquals(40.0, s.calculerPrix());
    }

    @Test
    void prixNulSansEspace() {
        Session s = session(LocalTime.of(9, 0), LocalTime.of(11, 0), null);
        assertEquals(0.0, s.calculerPrix());
    }

    @Test
    void intervenantsCopiesDansNouvelleListe() {
        ArrayList<String> source = new ArrayList<>(Arrays.asList("Alice"));
        Session s = new Session("C", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), source, salle(5), null);
        source.add("Intrus");
        assertEquals(1, s.getIntervenants().size());
    }

    @Test
    void intervenantsNullDonneListeVide() {
        Session s = new Session("C", LocalDate.of(2026, 6, 1), LocalTime.of(9, 0), LocalTime.of(10, 0), null, salle(5), null);
        assertTrue(s.getIntervenants().isEmpty());
    }

    @Test
    void ajoutEtRetraitIntervenant() {
        Session s = session(LocalTime.of(9, 0), LocalTime.of(10, 0), salle(5));
        s.addIntervenant("Carla");
        assertTrue(s.getIntervenants().contains("Carla"));
        s.removeIntervenant("Alice");
        assertFalse(s.getIntervenants().contains("Alice"));
    }
}
