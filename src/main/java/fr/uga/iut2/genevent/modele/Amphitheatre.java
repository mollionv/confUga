package fr.uga.iut2.genevent.modele;

import java.util.HashMap;

/**
 * Un amphithéâtre, espace de réunion qui possède en plus un nom.
 */
public class Amphitheatre extends EspaceReunion {

    private static final long serialVersionUID = 1L;
    private String nom;

    /**
     * Construit un amphithéâtre avec sa capacité, son équipement et son nom.
     * @param numero numéro de l'amphithéâtre
     * @param capacitePersonnes nombre de personnes maximum
     * @param nbVideoProjecteurs nombre de vidéoprojecteurs
     * @param nbOrdinateurs nombre d'ordinateurs
     * @param systemSonorisationPresent true si une sonorisation est présente
     * @param tarifHoraire tarif par heure
     * @param nbMicrophones nombre de microphones
     * @param nom nom de l'amphithéâtre
     */
    public Amphitheatre(int numero, int capacitePersonnes, int nbVideoProjecteurs, int nbOrdinateurs, boolean systemSonorisationPresent, float tarifHoraire, int nbMicrophones, String nom) {
        super(numero, capacitePersonnes, nbVideoProjecteurs, nbOrdinateurs, systemSonorisationPresent, tarifHoraire, nbMicrophones);
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return "Amphithéâtre " + nom + " (" + getCapacitePersonnes() + " pers.)";
    }
}
