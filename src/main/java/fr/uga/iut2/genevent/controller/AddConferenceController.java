package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.AppData;
import fr.uga.iut2.genevent.modele.Conference;
import fr.uga.iut2.genevent.modele.Session;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Contrôleur du formulaire d'ajout et de modification d'une conférence.
 */
public class AddConferenceController {

    @FXML
    private Label titreFormConferenceLabel;
    @FXML
    private TextField titreField;
    @FXML
    private TextField nomDemandeurField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private Button ajouterConferenceBtn;
    @FXML
    private GridPane gridPaneSessions;

    private AppData appData;
    private Conference conferenceCourante;
    private boolean nouvelleConference;

    /**
     * Prépare le formulaire en mode ajout ou modification.
     * @param appData les données de l'application
     * @param conferenceAModifier la conférence à modifier, ou null pour en créer une
     */
    public void init(AppData appData, Conference conferenceAModifier) {
        this.appData = appData;
        if (conferenceAModifier != null) {
            this.conferenceCourante = conferenceAModifier;
            this.nouvelleConference = false;
            titreFormConferenceLabel.setText("Modifier la conférence");
            titreField.setText(conferenceCourante.getNomConference());
            nomDemandeurField.setText(conferenceCourante.getNomReserveur());
            descriptionArea.setText(conferenceCourante.getDescription());
            ajouterConferenceBtn.setText("Enregistrer les modifications");
        } else {
            this.conferenceCourante = new Conference(0, "", "", "");
            this.nouvelleConference = true;
            titreFormConferenceLabel.setText("Ajouter une conférence");
            ajouterConferenceBtn.setText("+ Ajouter conférence");
        }
        refreshSessions();
    }

    /**
     * Marque les champs obligatoires au chargement de la fenêtre.
     */
    @FXML
    private void initialize() {
        Util.validerObligatoire(titreField);
        Util.validerObligatoire(nomDemandeurField);
        Util.validerObligatoire(descriptionArea);
    }

    /**
     * Réaffiche la liste des sessions de la conférence.
     */
    private void refreshSessions() {
        gridPaneSessions.getChildren().clear();

        int row = 0;
        for (Session session : conferenceCourante.getSessions()) {
            Label libelle = new Label(libelleSession(session));

            Button modifier = new Button("Modifier");
            modifier.setOnAction(e -> ouvrirSession(session));

            Button supprimer = new Button("Supprimer");
            supprimer.setOnAction(e -> {
                conferenceCourante.removeSession(session);
                Persisteur.sauvegarder(appData);
                refreshSessions();
            });

            HBox actions = new HBox(20.0, modifier, supprimer);
            actions.setAlignment(Pos.CENTER);

            GridPane.setColumnIndex(libelle, 0);
            GridPane.setRowIndex(libelle, row);
            GridPane.setColumnIndex(actions, 1);
            GridPane.setRowIndex(actions, row);
            gridPaneSessions.getChildren().addAll(libelle, actions);
            row++;
        }
    }

    /**
     * Construit le texte affiché pour une session dans la liste.
     * @param session la session concernée
     * @return le libellé "nom - date heureDebut-heureFin"
     */
    private String libelleSession(Session session) {
        String date = session.getDateReservation().format(Formats.DATE);
        String heureDebut = session.getHeureDebut().format(Formats.HEURE);
        String heureFin = session.getHeureFin().format(Formats.HEURE);
        return String.format("%s - %s %s-%s", session.getNom(), date, heureDebut, heureFin);
    }

    /**
     * Vérifie que tous les champs de la conférence sont remplis.
     * @return true si le formulaire est valide
     */
    private boolean validerChamps() {
        TextInputControl[] champs = {titreField, nomDemandeurField, descriptionArea};
        return Validation.champsRemplis("Veuillez remplir tous les champs de la conférence.", champs);
    }

    /**
     * Ouvre la fenêtre de création d'une nouvelle session.
     */
    @FXML
    private void actionButtonOpenNewSession() {
        ouvrirSession(null);
    }

    /**
     * Ouvre la fenêtre de session (création si null, sinon modification).
     * @param session la session à modifier, ou null pour en créer une
     */
    private void ouvrirSession(Session session) {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/session.fxml");
        SessionController controleur = loader.getController();
        controleur.init(appData, conferenceCourante, session);
        Fenetres.afficher(loader, 560, 750, "Session");
        refreshSessions();
    }

    /**
     * Enregistre la conférence (création ou modification) puis ferme la fenêtre.
     * @param event l'événement du clic
     */
    @FXML
    private void actionButtonAddConf(ActionEvent event) {
        if (validerChamps()) {
            conferenceCourante.setNomConference(titreField.getText().trim());
            conferenceCourante.setDescription(descriptionArea.getText().trim());
            conferenceCourante.setNomReserveur(nomDemandeurField.getText().trim());

            if (nouvelleConference) {
                appData.ajouterConference(conferenceCourante);
            }
            Persisteur.sauvegarder(appData);
            Fenetres.fermer(event);
        }
    }

    /**
     * Ferme la fenêtre sans enregistrer.
     * @param event l'événement du clic
     */
    @FXML
    private void actionButtonCancel(ActionEvent event) {
        Fenetres.fermer(event);
    }
}
