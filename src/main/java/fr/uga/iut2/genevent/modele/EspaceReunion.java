package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe mère des espaces de réunion (salle, amphithéâtre) et de leur équipement.
 */
public abstract class EspaceReunion implements Serializable {

    private static final long serialVersionUID = 1L;
    private int numero;
    private int capacitePersonnes;
    private int nbVideoProjecteurs;
    private int nbOrdinateurs;
    private boolean systemSonorisationPresent;
    private float tarifHoraire;
    private int nbMicrophones;
    private ArrayList<Session> estOccupePar;
    private Batiment appartientA;

    /**
     * Construit un espace de réunion avec sa capacité et son équipement.
     * @param numero numéro de l'espace
     * @param capacitePersonnes nombre de personnes maximum
     * @param nbVideoProjecteurs nombre de vidéoprojecteurs
     * @param nbOrdinateurs nombre d'ordinateurs
     * @param systemSonorisationPresent true si une sonorisation est présente
     * @param tarifHoraire tarif par heure
     * @param nbMicrophones nombre de microphones
     */
    public EspaceReunion(int numero, int capacitePersonnes, int nbVideoProjecteurs, int nbOrdinateurs, boolean systemSonorisationPresent, float tarifHoraire, int nbMicrophones) {
        this.numero = numero;
        this.capacitePersonnes = capacitePersonnes;
        this.nbVideoProjecteurs = nbVideoProjecteurs;
        this.nbOrdinateurs = nbOrdinateurs;
        this.systemSonorisationPresent = systemSonorisationPresent;
        this.tarifHoraire = tarifHoraire;
        this.nbMicrophones = nbMicrophones;
        this.estOccupePar = new ArrayList<>();
    }

    public int getNumero() {
        return numero;
    }

    public int getCapacitePersonnes() {
        return capacitePersonnes;
    }

    public int getNbVideoProjecteurs() {
        return nbVideoProjecteurs;
    }

    public int getNbOrdinateurs() {
        return nbOrdinateurs;
    }

    public boolean isSystemSonorisationPresent() {
        return systemSonorisationPresent;
    }

    public float getTarifHoraire() {
        return tarifHoraire;
    }

    public int getNbMicrophones() {
        return nbMicrophones;
    }

    public ArrayList<Session> getReservations() {
        return estOccupePar;
    }

    public Batiment getAppartientA() {
        return appartientA;
    }

    public void setAppartientA(Batiment appartientA) {
        this.appartientA = appartientA;
    }

    /**
     * Calcule le prix d'une réservation selon le tarif horaire de l'espace.
     * @param nbHeures la durée de la réservation en heures
     * @return le prix de la réservation
     */
    public double calculerPrix(double nbHeures) {
        return nbHeures * tarifHoraire;
    }
}
