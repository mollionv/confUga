package fr.uga.iut2.genevent.modele;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BatimentTest {

    @Test
    void ajoutEspaceLieLeBatiment() {
        Batiment batiment = new Batiment("IM2AG", "rue", "Grenoble", 38000);
        Salle salle = new Salle(1, 20, 0, 0, false, 10, 0);
        batiment.addEspace(salle);
        assertEquals(1, batiment.getListeEspaces().size());
        assertSame(batiment, salle.getAppartientA());
    }

    @Test
    void retraitEspace() {
        Batiment batiment = new Batiment("IM2AG", "rue", "Grenoble", 38000);
        Salle salle = new Salle(1, 20, 0, 0, false, 10, 0);
        batiment.addEspace(salle);
        batiment.removeEspace(salle);
        assertTrue(batiment.getListeEspaces().isEmpty());
    }

    @Test
    void toStringEstLeNom() {
        Batiment batiment = new Batiment("IMAG", "rue", "Grenoble", 38000);
        assertEquals("IMAG", batiment.toString());
    }

    @Test
    void listeEspacesVideALaCreation() {
        Batiment batiment = new Batiment("IMAG", "rue", "Grenoble", 38000);
        assertTrue(batiment.getListeEspaces().isEmpty());
    }
}
