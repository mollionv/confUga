package fr.uga.iut2.genevent.modele;

import java.util.HashMap;

/**
 * Une salle, c'est-à-dire un espace de réunion identifié par son numéro.
 */
public class Salle extends EspaceReunion {

    private static final long serialVersionUID = 1L;

    /**
     * Construit une salle avec sa capacité et son équipement.
     * @param numero numéro de la salle
     * @param capacitePersonnes nombre de personnes maximum
     * @param nbVideoProjecteurs nombre de vidéoprojecteurs
     * @param nbOrdinateurs nombre d'ordinateurs
     * @param systemSonorisationPresent true si une sonorisation est présente
     * @param tarifHoraire tarif par heure
     * @param nbMicrophones nombre de microphones
     */
    public Salle(int numero, int capacitePersonnes, int nbVideoProjecteurs, int nbOrdinateurs, boolean systemSonorisationPresent, int tarifHoraire, int nbMicrophones) {
        super(numero, capacitePersonnes, nbVideoProjecteurs, nbOrdinateurs, systemSonorisationPresent, tarifHoraire, nbMicrophones);
    }

    @Override
    public String toString() {
        return "Salle n°" + getNumero() + " (" + getCapacitePersonnes() + " pers.)";
    }
}
