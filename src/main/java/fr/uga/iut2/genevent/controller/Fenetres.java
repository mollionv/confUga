package fr.uga.iut2.genevent.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Méthodes utilitaires pour ouvrir et fermer les fenêtres de l'application.
 */
public class Fenetres {

    private Fenetres() {
    }

    /**
     * Charge un fichier FXML et prépare son contrôleur.
     * @param view le chemin du fichier FXML
     * @return le loader chargé (pour récupérer le contrôleur)
     */
    public static FXMLLoader charger(String view) {
        try {
            FXMLLoader loader = new FXMLLoader(Fenetres.class.getResource(view));
            loader.load();
            return loader;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ouvre une fenêtre modale et attend sa fermeture.
     * @param loader le loader déjà chargé
     * @param largeur la largeur de la fenêtre
     * @param hauteur la hauteur de la fenêtre
     * @param titre le titre de la fenêtre
     */
    public static void afficher(FXMLLoader loader, int largeur, int hauteur, String titre) {
        Parent racine = loader.getRoot();
        Stage fenetre = new Stage();
        Scene scene = new Scene(racine, largeur, hauteur);
        scene.getStylesheets().add(Fenetres.class.getResource("/fr/uga/iut2/genevent/view/style.css").toExternalForm());
        fenetre.getIcons().add(new Image(Fenetres.class.getResourceAsStream("/fr/uga/iut2/genevent/view/icon.png")));
        fenetre.setScene(scene);
        fenetre.setTitle(titre);
        fenetre.setResizable(false);
        fenetre.initModality(Modality.APPLICATION_MODAL);
        fenetre.showAndWait();
    }

    /**
     * Ferme la fenêtre qui contient le bouton cliqué.
     * @param event l'événement du clic
     */
    public static void fermer(ActionEvent event) {
        Stage fenetre = (Stage) ((Node) event.getSource()).getScene().getWindow();
        fenetre.close();
    }
}
