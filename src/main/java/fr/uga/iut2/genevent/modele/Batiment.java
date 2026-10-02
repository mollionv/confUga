package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Un bâtiment qui contient des espaces de réunion (salles, amphithéâtres).
 */
public class Batiment implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nomBatiment;
    private String adresse;
    private String ville;
    private int codePostal;
    private ArrayList<EspaceReunion> listeEspaces = new ArrayList<>();

    /**
     * Construit un nouveau bâtiment, sans aucun espace de réunion au départ.
     * @param nomBatiment nom unique du bâtiment
     * @param adresse adresse postale
     * @param ville ville
     * @param codePostal code postal
     */
    public Batiment(String nomBatiment, String adresse, String ville, int codePostal) {
        this.nomBatiment = nomBatiment;
        this.adresse = adresse;
        this.ville = ville;
        this.codePostal = codePostal;
    }

    public String getNomBatiment() {
        return this.nomBatiment;
    }

    public void setNomBatiment(String nomBatiment) {
        this.nomBatiment = nomBatiment;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return this.ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getCodePostal() {
        return this.codePostal;
    }

    public void setCodePostal(int codePostal) {
        this.codePostal = codePostal;
    }

    public ArrayList<EspaceReunion> getListeEspaces() {
        return this.listeEspaces;
    }

    /**
     * Ajoute un espace de réunion au bâtiment et le relie à celui-ci.
     * @param espace l'espace de réunion à ajouter
     */
    public void addEspace(EspaceReunion espace) {
        espace.setAppartientA(this);
        this.listeEspaces.add(espace);
    }

    /**
     * Retire un espace de réunion du bâtiment.
     * @param espace l'espace de réunion à retirer
     */
    public void removeEspace(EspaceReunion espace) {
        this.listeEspaces.remove(espace);
    }

    @Override
    public String toString() {
        return this.nomBatiment;
    }
}
