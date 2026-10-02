package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.Conference;
import fr.uga.iut2.genevent.modele.EspaceReunion;
import fr.uga.iut2.genevent.modele.Session;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Génère le texte d'un fichier calendrier (.ics) à partir d'une conférence.
 */
public class CalendrierIcs {

    private static final DateTimeFormatter HORODATAGE = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
    private static final int LONGUEUR_LIGNE = 70;

    /**
     * Construit le contenu .ics de toutes les sessions de la conférence.
     * @param conference la conférence à exporter
     * @return le texte complet du fichier .ics
     */
    public static String exporter(Conference conference) {
        StringBuilder ics = new StringBuilder();
        ajouterLigne(ics, "BEGIN:VCALENDAR");
        ajouterLigne(ics, "VERSION:2.0");
        ajouterLigne(ics, "PRODID:-//uConf//Calendrier conference//FR");
        ajouterLigne(ics, "CALSCALE:GREGORIAN");
        ajouterLigne(ics, "METHOD:PUBLISH");
        ajouterLigne(ics, "X-WR-CALNAME:" + echapper(conference.getNomConference()));

        List<Session> sessions = new ArrayList<>(conference.getSessions());
        sessions.sort((s1, s2) -> {
            int parDate = s1.getDateReservation().compareTo(s2.getDateReservation());
            if (parDate != 0) {
                return parDate;
            }
            return s1.getHeureDebut().compareTo(s2.getHeureDebut());
        });

        String horodatage = LocalDateTime.now().format(HORODATAGE);
        int index = 0;
        for (Session session : sessions) {
            index++;
            ajouterEvenement(ics, conference, session, index, horodatage);
        }

        ajouterLigne(ics, "END:VCALENDAR");
        return ics.toString();
    }

    /**
     * Ajoute un évènement (VEVENT) correspondant à une session.
     * @param ics le texte en construction
     * @param conference la conférence concernée
     * @param session la session à ajouter
     * @param index le numéro de la session (pour l'identifiant)
     * @param horodatage la date de génération du fichier
     */
    private static void ajouterEvenement(StringBuilder ics, Conference conference, Session session, int index, String horodatage) {
        LocalDate date = session.getDateReservation();
        LocalDateTime debut = LocalDateTime.of(date, session.getHeureDebut());
        LocalDateTime fin = LocalDateTime.of(date, session.getHeureFin());

        ajouterLigne(ics, "BEGIN:VEVENT");
        ajouterLigne(ics, "UID:conf" + conference.getNumConference() + "-" + debut.format(HORODATAGE) + "-" + index + "@uconf");
        ajouterLigne(ics, "DTSTAMP:" + horodatage);
        ajouterLigne(ics, "DTSTART:" + debut.format(HORODATAGE));
        ajouterLigne(ics, "DTEND:" + fin.format(HORODATAGE));
        ajouterLigne(ics, "SUMMARY:" + echapper(session.getNom()));
        ajouterLigne(ics, "LOCATION:" + echapper(lieu(session)));
        ajouterLigne(ics, "DESCRIPTION:" + echapper(description(conference, session)));
        ajouterLigne(ics, "END:VEVENT");
    }

    /**
     * Construit le texte du lieu d'une session (espace et bâtiment).
     * @param session la session concernée
     * @return le lieu, ou une chaîne vide si aucun espace
     */
    private static String lieu(Session session) {
        EspaceReunion espace = session.getOccupe();
        if (espace == null) {
            return "";
        }
        if (espace.getAppartientA() == null) {
            return espace.toString();
        }
        return espace.toString() + " - " + espace.getAppartientA().getNomBatiment();
    }

    /**
     * Construit le texte de description d'une session (conférence, intervenants, tarif).
     * @param conference la conférence concernée
     * @param session la session concernée
     * @return le texte de la description
     */
    private static String description(Conference conference, Session session) {
        StringBuilder description = new StringBuilder();
        description.append("Conference : ").append(conference.getNomConference());
        if (!session.getIntervenants().isEmpty()) {
            description.append("\n").append("Intervenant(s) : ").append(String.join(", ", session.getIntervenants()));
        }
        description.append("\n").append("Tarif : ").append(String.format("%.2f€", session.calculerPrix()));
        return description.toString();
    }

    /**
     * Protège les caractères spéciaux du format .ics.
     * @param valeur le texte à échapper
     * @return le texte échappé (vide si null)
     */
    private static String echapper(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\n", "\\n");
    }

    /**
     * Ajoute une ligne au fichier en la coupant si elle est trop longue.
     * @param ics le texte en construction
     * @param ligne la ligne à ajouter
     */
    private static void ajouterLigne(StringBuilder ics, String ligne) {
        int debut = 0;
        while (ligne.length() - debut > LONGUEUR_LIGNE) {
            ics.append(ligne, debut, debut + LONGUEUR_LIGNE).append("\r\n ");
            debut += LONGUEUR_LIGNE;
        }
        ics.append(ligne, debut, ligne.length()).append("\r\n");
    }
}
