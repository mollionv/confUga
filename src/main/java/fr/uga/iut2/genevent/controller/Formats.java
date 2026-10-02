package fr.uga.iut2.genevent.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Formats d'affichage des dates et des heures utilisés dans l'application.
 */
public class Formats {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter HEURE = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter JOUR_MOIS = DateTimeFormatter.ofPattern("dd/MM");
    private static final String[] JOURS = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};

    /**
     * Donne le jour de la semaine suivi de la date (ex : "Lundi 01/06").
     * @param date la date à afficher
     * @return le texte du jour avec sa date
     */
    public static String jour(LocalDate date) {
        return JOURS[date.getDayOfWeek().getValue() - 1] + " " + date.format(JOUR_MOIS);
    }
}
