package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Données de l'application : la liste des conférences et des bâtiments.
 */
public class AppData implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<Conference> conferences;
    private ArrayList<Batiment> batiments;

    /**
     * Construit des données vides, sans aucune conférence ni bâtiment.
     */
    public AppData() {
        this.conferences = new ArrayList<>();
        this.batiments = new ArrayList<>();
    }

    public ArrayList<Conference> getConferences() {
        return conferences;
    }

    public ArrayList<Batiment> getBatiments() {
        return batiments;
    }

    /**
     * Ajoute une conférence à l'application en lui attribuant
     * automatiquement le prochain numéro disponible.
     * @param conference la conférence à ajouter
     */
    public void ajouterConference(Conference conference) {
        int numero = 0;
        for (Conference conf : conferences) {
            if (conf.getNumConference() > numero) {
                numero = conf.getNumConference();
            }
        }
        conference.setNumConference(numero + 1);
        conferences.add(conference);
    }

    /**
     * Retire une conférence de l'application.
     * @param conference la conférence à supprimer
     */
    public void supprimerConference(Conference conference) {
        conferences.remove(conference);
    }

    /**
     * Crée un nouveau bâtiment et l'ajoute à l'application.
     * @param nomBat nom du bâtiment
     * @param adresse adresse postale
     * @param ville ville
     * @param codePostal code postal
     * @return le bâtiment créé
     */
    public Batiment ajouterBatiment(String nomBat, String adresse, String ville, int codePostal) {
        Batiment batiment = new Batiment(nomBat, adresse, ville, codePostal);
        batiments.add(batiment);
        return batiment;
    }

    /**
     * Retire un bâtiment de l'application.
     * @param batiment le bâtiment à supprimer
     */
    public void supprimerBatiment(Batiment batiment) {
        batiments.remove(batiment);
    }

    /**
     * Cherche toutes les sessions qui occupent déjà l'espace donné
     * et qui chevauchent le créneau demandé.
     * @param espace l'espace de réunion concerné
     * @param date la date du créneau
     * @param debut l'heure de début du créneau
     * @param fin l'heure de fin du créneau
     * @param aExclure une session à ignorer (utile en modification), ou null
     * @param conferenceEnCours conférence en cours d'édition à inclure dans la recherche, ou null
     * @return la liste des sessions en conflit (vide s'il n'y en a pas)
     */
    public List<Session> sessionsEnConflit(EspaceReunion espace, LocalDate date, LocalTime debut, LocalTime fin, Session aExclure, Conference conferenceEnCours) {
        List<Session> conflits = new ArrayList<>();
        if (espace != null && date != null && debut != null && fin != null) {
            List<Conference> aVerifier = new ArrayList<>(conferences);
            if (conferenceEnCours != null && !aVerifier.contains(conferenceEnCours)) {
                aVerifier.add(conferenceEnCours);
            }
            for (Conference conference : aVerifier) {
                for (Session session : conference.getSessions()) {
                    if (session != aExclure && chevauche(session, espace, date, debut, fin)) {
                        conflits.add(session);
                    }
                }
            }
        }
        return conflits;
    }

    /**
     * Teste si une session occupe le même espace, le même jour,
     * et si ses horaires se chevauchent avec le créneau donné.
     * @param session la session à tester
     * @param espace l'espace de réunion visé
     * @param date la date du créneau
     * @param debut l'heure de début du créneau
     * @param fin l'heure de fin du créneau
     * @return true si la session est en conflit avec le créneau
     */
    private boolean chevauche(Session session, EspaceReunion espace, LocalDate date, LocalTime debut, LocalTime fin) {
        if (session.getOccupe() != espace || !date.equals(session.getDateReservation())) {
            return false;
        }
        LocalTime autreDebut = session.getHeureDebut();
        LocalTime autreFin = session.getHeureFin();
        return autreDebut != null && autreFin != null
                && debut.isBefore(autreFin) && autreDebut.isBefore(fin);
    }

    /**
     * Indique si l'espace est libre sur le créneau demandé,
     * c'est-à-dire s'il n'existe aucune session en conflit.
     * @param espace l'espace de réunion concerné
     * @param date la date du créneau
     * @param debut l'heure de début du créneau
     * @param fin l'heure de fin du créneau
     * @param aExclure une session à ignorer (utile en modification), ou null
     * @param conferenceEnCours conférence en cours d'édition à inclure dans la recherche, ou null
     * @return true si l'espace est disponible
     */
    public boolean estDisponible(EspaceReunion espace, LocalDate date, LocalTime debut, LocalTime fin, Session aExclure, Conference conferenceEnCours) {
        return sessionsEnConflit(espace, date, debut, fin, aExclure, conferenceEnCours).isEmpty();
    }
}
