package fr.uga.iut2.genevent.basefournie.modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class Evenement implements Serializable {

    private static final long serialVersionUID = 1L;  // nécessaire pour la sérialisation
    private final GenEvent genevent;
    private final String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private final Map<String, Utilisateur> administrateurs;  // association qualifiée par l'email

    // Invariant de classe : !dateDebut.isAfter(dateFin)
    //     On utilise la négation ici pour exprimer (dateDebut <= dateFin), ce
    //     qui est équivalent à !(dateDebut > dateFin).

    public static Evenement initialiseEvenement(GenEvent genevent, String nom, LocalDate dateDebut, LocalDate dateFin, Utilisateur admin) {
        Evenement evt = new Evenement(genevent, nom, dateDebut, dateFin);
        evt.ajouteAdministrateur(admin);
        return evt;
    }

    public Evenement(GenEvent genevent, String nom, LocalDate dateDebut, LocalDate dateFin) {
        assert !dateDebut.isAfter(dateFin);
        this.genevent = genevent;
        this.nom = nom;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.administrateurs = new HashMap<>();
    }

    public String getNom() {
        return this.nom;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        assert !dateDebut.isAfter(this.dateFin);
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        assert !this.dateDebut.isAfter(dateFin);
        this.dateFin = dateFin;
    }

    public void ajouteAdministrateur(Utilisateur admin) {
        assert !this.administrateurs.containsKey(admin.getEmail());
        this.administrateurs.put(admin.getEmail(), admin);
        admin.ajouteEvenementAdministre(this);
    }
}
