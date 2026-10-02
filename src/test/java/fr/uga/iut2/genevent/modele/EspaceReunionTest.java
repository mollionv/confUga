package fr.uga.iut2.genevent.modele;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EspaceReunionTest {

    @Test
    void prixSalleProportionnelAuTarif() {
        Salle salle = new Salle(1, 20, 0, 0, false, 15, 0);
        assertEquals(45.0, salle.calculerPrix(3.0));
    }

    @Test
    void prixAmphiProportionnelAuTarif() {
        Amphitheatre amphi = new Amphitheatre(2, 100, 2, 0, true, 30f, 4, "A");
        assertEquals(60.0, amphi.calculerPrix(2.0));
    }

    @Test
    void prixNulSiZeroHeure() {
        Salle salle = new Salle(1, 20, 0, 0, false, 15, 0);
        assertEquals(0.0, salle.calculerPrix(0.0));
    }

    @Test
    void toStringSalleContientNumero() {
        Salle salle = new Salle(7, 20, 0, 0, false, 15, 0);
        assertTrue(salle.toString().contains("7"));
    }

    @Test
    void toStringAmphiContientNom() {
        Amphitheatre amphi = new Amphitheatre(2, 100, 2, 0, true, 30f, 4, "Weil");
        assertTrue(amphi.toString().contains("Weil"));
    }
}
