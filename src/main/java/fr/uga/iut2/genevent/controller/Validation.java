package fr.uga.iut2.genevent.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputControl;

import java.util.Optional;

/**
 * Méthodes utilitaires pour afficher des messages et vérifier les champs.
 */
public class Validation {

    /**
     * Affiche une boîte de dialogue.
     * @param message le texte à afficher
     * @param alertType le type d'alerte (avertissement, info...)
     * @param avecAnnuler true pour ajouter un bouton Annuler
     * @return le bouton cliqué par l'utilisateur
     */
    private static Optional<ButtonType> afficherAlerte(String message, Alert.AlertType alertType, boolean avecAnnuler) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (avecAnnuler) {
            ButtonType btnOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            ButtonType btnAnnuler = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnOk, btnAnnuler);
        }
        return alert.showAndWait();
    }

    /**
     * Demande une confirmation à l'utilisateur.
     * @param message la question posée
     * @return true si l'utilisateur clique sur OK
     */
    public static boolean confirmer(String message) {
        Optional<ButtonType> reponse = afficherAlerte(message, Alert.AlertType.CONFIRMATION, true);
        return reponse.isPresent() && reponse.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    /**
     * Dit si un champ texte est vide.
     * @param champ le champ à tester
     * @return true si le champ est vide
     */
    public static boolean estVide(TextInputControl champ) {
        return champ == null || champ.getText() == null || champ.getText().trim().isEmpty();
    }

    /**
     * Vérifie que tous les champs sont remplis, sinon affiche une alerte.
     * @param message le message d'erreur si un champ est vide
     * @param champs les champs à contrôler
     * @return true si tous les champs sont remplis
     */
    public static boolean champsRemplis(String message, TextInputControl[] champs) {
        for (TextInputControl champ : champs) {
            if (estVide(champ)) {
                alerte(message);
                return false;
            }
        }
        return true;
    }

    /**
     * Affiche un avertissement.
     * @param message le texte à afficher
     */
    public static void alerte(String message) {
        afficherAlerte(message, Alert.AlertType.WARNING, false);
    }

    /**
     * Affiche une information.
     * @param message le texte à afficher
     */
    public static void information(String message) {
        afficherAlerte(message, Alert.AlertType.INFORMATION, false);
    }
}
