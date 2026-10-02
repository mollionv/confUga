package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.AppData;
import fr.uga.iut2.genevent.modele.Batiment;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

/**
 * Contrôleur de la fenêtre de gestion des bâtiments.
 */
public class GestionBatimentsController {

    @FXML
    private FlowPane batimentContainer;

    private AppData appData;

    /**
     * Initialise le contrôleur avec les données de l'application.
     * @param appData les données de l'application
     */
    public void init(AppData appData) {
        this.appData = appData;
        refresh();
    }

    /**
     * Réaffiche la carte "+" et toutes les cartes de bâtiments.
     */
    private void refresh() {
        batimentContainer.getChildren().clear();
        batimentContainer.getChildren().add(creerCarteAjouter());
        for (Batiment bat : appData.getBatiments()) {
            batimentContainer.getChildren().add(creerCarteBatiment(bat));
        }
    }

    /**
     * Crée la carte "+" qui ouvre la création d'un bâtiment.
     * @return la carte d'ajout
     */
    private VBox creerCarteAjouter() {
        VBox carte = new VBox();
        carte.getStyleClass().add("card");
        carte.setCursor(Cursor.HAND);
        carte.setAlignment(Pos.CENTER);
        carte.setPrefWidth(240.0);
        carte.setPrefHeight(260.0);
        Label addLabel = new Label("+");
        addLabel.getStyleClass().add("addCard");
        carte.getChildren().add(addLabel);
        carte.setOnMouseClicked(e -> nouveauBatiment());
        return carte;
    }

    /**
     * Crée la carte qui affiche les infos d'un bâtiment.
     * @param bat le bâtiment à afficher
     * @return la carte du bâtiment
     */
    private VBox creerCarteBatiment(Batiment bat) {
        VBox carte = Cartes.carte();

        Label nom = new Label(bat.getNomBatiment());
        HBox ligneAdresse = Cartes.ligne("Adresse :", bat.getAdresse());
        HBox ligneVille = Cartes.ligne("Ville :", bat.getVille());
        HBox ligneCodePostal = Cartes.ligne("Code postal :", Integer.toString(bat.getCodePostal()));

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button detailsBtn = new Button("Détails et modifications");
        detailsBtn.setOnAction(e -> modifierBatiment(bat));

        SVGPath icone = new SVGPath();
        icone.setContent("M6 19c0 1.1 .9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z");
        icone.getStyleClass().add("icon-trash");
        Button supprimerBtn = new Button();
        supprimerBtn.setGraphic(icone);
        supprimerBtn.getStyleClass().add("icon-button");
        supprimerBtn.setTooltip(new Tooltip("Supprimer le bâtiment"));
        supprimerBtn.setOnAction(e -> supprimerBatiment(bat));

        HBox boutonBox = new HBox(8.0, detailsBtn, supprimerBtn);
        boutonBox.setAlignment(Pos.CENTER);


        carte.getChildren().addAll(
                nom,
                ligneAdresse,
                ligneVille,
                ligneCodePostal,
                spacer,
                boutonBox
        );
        return carte;
    }

    /**
     * Ouvre la création d'un bâtiment (bouton du haut).
     */
    @FXML
    private void actionButtonOpenNewBat() {
        nouveauBatiment();
    }

    /**
     * Ouvre la fenêtre pour créer un nouveau bâtiment.
     */
    private void nouveauBatiment() {
        ouvrirEditBatiment(null);
    }

    /**
     * Ouvre la fenêtre pour modifier un bâtiment existant.
     * @param batiment le bâtiment à modifier
     */
    private void modifierBatiment(Batiment batiment) {
        ouvrirEditBatiment(batiment);
    }

    /**
     * Supprime un bâtiment après confirmation de l'utilisateur.
     * @param batiment le bâtiment à supprimer
     */
    private void supprimerBatiment(Batiment batiment) {
        String msg = String.format("Supprimer définitivement le bâtiment « %s » ?", batiment.getNomBatiment());
        if (Validation.confirmer(msg)) {
            appData.supprimerBatiment(batiment);
            Persisteur.sauvegarder(appData);
            refresh();
        }
    }

    /**
     * Ouvre la fenêtre d'édition d'un bâtiment (création si null).
     * @param batiment le bâtiment à éditer, ou null pour en créer un
     */
    private void ouvrirEditBatiment(Batiment batiment) {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/edit-batiment.fxml");
        EditBatimentController controleur = loader.getController();
        controleur.init(appData, batiment);
        Fenetres.afficher(loader, 560, 745, "Bâtiment");
        refresh();
    }
}
