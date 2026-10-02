package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.AppData;
import fr.uga.iut2.genevent.modele.Conference;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Contrôleur de la page d'accueil : affiche la liste des conférences.
 */
public class HomeController {

    @FXML
    private FlowPane conferencesContainer;

    private AppData appData;

    /**
     * Charge les données sauvegardées au démarrage et affiche les conférences.
     */
    @FXML
    private void initialize() {
        try {
            appData = Persisteur.lireEtat();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erreur au chargement des données : " + e.getMessage());
            appData = new AppData();
        }
        refresh();
    }

    /**
     * Réaffiche la carte "+" et toutes les cartes de conférences.
     */
    private void refresh() {
        conferencesContainer.getChildren().clear();
        conferencesContainer.getChildren().add(creerCarteAjouter());
        for (Conference conf : appData.getConferences()) {
            conferencesContainer.getChildren().add(creerCarteConference(conf));
        }
    }

    /**
     * Crée la carte "+" qui ouvre la création d'une conférence.
     * @return la carte d'ajout
     */
    private VBox creerCarteAjouter() {
        VBox carte = new VBox();
        carte.getStyleClass().add("card");
        carte.setCursor(Cursor.HAND);
        carte.setAlignment(Pos.CENTER);
        carte.getStyleClass().add("addCard");
        carte.setPrefWidth(240.0);
        carte.setPrefHeight(260.0);
        Label labelPlus = new Label("+");
        labelPlus.getStyleClass().add("addCard");
        carte.getChildren().add(labelPlus);
        carte.setOnMouseClicked(e -> nouvelleConference());
        return carte;
    }

    /**
     * Crée la carte qui affiche les infos d'une conférence.
     * @param conf la conférence à afficher
     * @return la carte de la conférence
     */
    private VBox creerCarteConference(Conference conf) {
        VBox carte = Cartes.carte();

        Label nom = new Label(conf.getNomConference());
        nom.getStyleClass().add("card-title");

        LocalDate debut = conf.getDebut();
        LocalDate fin = conf.getFin();
        HBox ligneDebut = Cartes.ligne("Date début :", debut != null ? debut.format(Formats.DATE) : "-");
        HBox ligneFin = Cartes.ligne("Date fin :", fin != null ? fin.format(Formats.DATE) : "-");

        Label labelDesc = new Label("Description :");
        Label valeurDesc = new Label(conf.getDescription());
        valeurDesc.setWrapText(true);
        valeurDesc.setMaxWidth(210.0);

        HBox ligneDemandeur = Cartes.ligne("Demandeur :", conf.getNomReserveur());

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button detailsBtn = new Button("Détails et modifications");
        detailsBtn.setOnAction(e -> voirDetails(conf));

        HBox boutonBox = new HBox(detailsBtn);
        boutonBox.setAlignment(Pos.CENTER);

        carte.getChildren().addAll(
                nom,
                ligneDebut,
                ligneFin,
                labelDesc,
                valeurDesc,
                ligneDemandeur,
                spacer,
                boutonBox
        );
        return carte;
    }

    /**
     * Ouvre la fenêtre de gestion des bâtiments.
     */
    @FXML
    private void actionButtonOpenBatiment() {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/gestion_batiments.fxml");
        GestionBatimentsController controleur = loader.getController();
        controleur.init(appData);
        Fenetres.afficher(loader, 890, 600, "Gestion des bâtiments");
        refresh();
    }

    /**
     * Ouvre la fenêtre de création d'une conférence (bouton du haut).
     */
    @FXML
    private void actionButtonOpenNewConf() {
        nouvelleConference();
    }

    /**
     * Ouvre la fenêtre de création d'une nouvelle conférence.
     */
    private void nouvelleConference() {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/add-conference.fxml");
        AddConferenceController controleur = loader.getController();
        controleur.init(appData, null);
        Fenetres.afficher(loader, 560, 725, "Nouvelle conférence");
        refresh();
    }

    /**
     * Ouvre la fenêtre de détails d'une conférence.
     * @param conference la conférence à afficher
     */
    private void voirDetails(Conference conference) {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/details-conference.fxml");
        DetailsConferenceController controleur = loader.getController();
        controleur.init(appData, conference);
        Fenetres.afficher(loader, 1280, 800, "Détails de la conférence");
        refresh();
    }
}
