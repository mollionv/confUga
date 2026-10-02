package fr.uga.iut2.genevent.controller;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Méthodes utilitaires pour créer les cartes affichées dans les listes.
 */
public class Cartes {

    /**
     * Crée une carte vide avec le style et la taille par défaut.
     * @return la carte (VBox) prête à être remplie
     */
    public static VBox carte() {
        VBox carte = new VBox(8.0);
        carte.getStyleClass().add("card");
        carte.setPrefWidth(240.0);
        carte.setPrefHeight(260.0);
        carte.setPadding(new Insets(16.0));
        return carte;
    }

    /**
     * Crée une ligne "libellé : valeur" pour une carte.
     * @param libelle le texte de gauche
     * @param valeur le texte de droite
     * @return la ligne (HBox) avec les deux labels
     */
    public static HBox ligne(String libelle, String valeur) {
        return new HBox(4.0, new Label(libelle), new Label(valeur));
    }
}
