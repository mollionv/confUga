package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.AppData;
import fr.uga.iut2.genevent.modele.Batiment;
import fr.uga.iut2.genevent.modele.EspaceReunion;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Contrôleur du formulaire d'ajout et de modification d'un bâtiment.
 */
public class EditBatimentController {

    @FXML
    private Label nomBatiment;
    @FXML
    private TextField nomBatimentField;
    @FXML
    private TextField adresseBatimentField;
    @FXML
    private TextField villeBatimentField;
    @FXML
    private TextField codePostalBatimentField;
    @FXML
    private Button ajouterBatimentBtn;
    @FXML
    private GridPane gridPaneEspacesReunion;

    private AppData appData;
    private Batiment batimentCourant;

    /**
     * Prépare le formulaire en mode ajout ou modification.
     * @param appData les données de l'application
     * @param batimentAModifier le bâtiment à modifier, ou null pour en créer un
     */
    public void init(AppData appData, Batiment batimentAModifier) {
        this.appData = appData;
        this.batimentCourant = batimentAModifier;
        appliquerEtatFormulaire();
        refreshEspaces();
    }

    /**
     * Configure les contraintes de saisie au chargement de la fenêtre.
     */
    @FXML
    private void initialize() {
        Util.forceNumericValue(codePostalBatimentField);
        Util.limitLength(codePostalBatimentField, 5);
        Util.validerObligatoire(nomBatimentField);
        Util.validerObligatoire(adresseBatimentField);
        Util.validerObligatoire(villeBatimentField);
        Util.validerObligatoire(codePostalBatimentField);
    }

    /**
     * Remplit les champs et adapte les textes selon le mode (ajout ou modification).
     */
    private void appliquerEtatFormulaire() {
        if (batimentCourant != null) {
            nomBatiment.setText("Modifier le bâtiment");
            nomBatimentField.setText(batimentCourant.getNomBatiment());
            adresseBatimentField.setText(batimentCourant.getAdresse());
            villeBatimentField.setText(batimentCourant.getVille());
            codePostalBatimentField.setText(Integer.toString(batimentCourant.getCodePostal()));
            ajouterBatimentBtn.setText("Enregistrer les modifications");
        } else {
            nomBatiment.setText("Ajouter un batiment");
            ajouterBatimentBtn.setText("Ajouter le bâtiment");
        }
    }

    /**
     * Réaffiche la liste des espaces de réunion du bâtiment.
     */
    private void refreshEspaces() {
        gridPaneEspacesReunion.getChildren().clear();
        if (batimentCourant != null) {
            int row = 0;
            for (EspaceReunion espace : batimentCourant.getListeEspaces()) {
                Label libelle = new Label(espace.toString());

                Button modifier = new Button("Modifier");
                modifier.setOnAction(e -> ouvrirEspace(espace));

                Button supprimer = new Button("Supprimer");
                supprimer.setOnAction(e -> {
                    batimentCourant.removeEspace(espace);
                    Persisteur.sauvegarder(appData);
                    refreshEspaces();
                });

                HBox actions = new HBox(20.0, modifier, supprimer);
                actions.setAlignment(Pos.CENTER);

                GridPane.setColumnIndex(libelle, 0);
                GridPane.setRowIndex(libelle, row);
                GridPane.setColumnIndex(actions, 1);
                GridPane.setRowIndex(actions, row);
                gridPaneEspacesReunion.getChildren().addAll(libelle, actions);
                row++;
            }
        }
    }

    /**
     * Vérifie que les champs sont remplis et que le code postal est un nombre.
     * @return true si le formulaire est valide
     */
    private boolean validerChamps() {
        TextInputControl[] champs = {nomBatimentField, adresseBatimentField, villeBatimentField, codePostalBatimentField};
        if (!Validation.champsRemplis("Veuillez remplir tous les champs du bâtiment.", champs)) {
            return false;
        }
        try {
            Integer.parseInt(codePostalBatimentField.getText().trim());
        } catch (NumberFormatException e) {
            Validation.alerte("Le code postal doit être un nombre.");
            return false;
        }
        return true;
    }

    /**
     * Ouvre la création d'un espace (crée d'abord le bâtiment s'il n'existe pas).
     */
    @FXML
    private void actionButtonOpenNewEspace() {
        if (batimentCourant == null) {
            if (!validerChamps()) {
                return;
            }
            batimentCourant = appData.ajouterBatiment(nomBatimentField.getText().trim(), adresseBatimentField.getText().trim(), villeBatimentField.getText().trim(), Integer.parseInt(codePostalBatimentField.getText().trim()));
            Persisteur.sauvegarder(appData);
            appliquerEtatFormulaire();
        }
        ouvrirEspace(null);
    }

    /**
     * Ouvre la fenêtre d'un espace de réunion (création si null).
     * @param espace l'espace à modifier, ou null pour en créer un
     */
    private void ouvrirEspace(EspaceReunion espace) {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/espace-reunion.fxml");
        EspaceReunionController controleur = loader.getController();
        controleur.init(appData, batimentCourant, espace);
        Fenetres.afficher(loader, 560, 1000, "Espace de réunion");
        refreshEspaces();
    }

    /**
     * Enregistre le bâtiment (création ou modification) puis ferme la fenêtre.
     * @param event l'événement du clic
     */
    @FXML
    private void actionButtonAddBat(ActionEvent event) {
        if (validerChamps()) {
            String nom = nomBatimentField.getText().trim();
            String adresse = adresseBatimentField.getText().trim();
            String ville = villeBatimentField.getText().trim();
            int codePostal = Integer.parseInt(codePostalBatimentField.getText().trim());

            if (batimentCourant == null) {
                appData.ajouterBatiment(nom, adresse, ville, codePostal);
            } else {
                batimentCourant.setNomBatiment(nom);
                batimentCourant.setAdresse(adresse);
                batimentCourant.setVille(ville);
                batimentCourant.setCodePostal(codePostal);
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
