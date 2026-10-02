package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.AppData;
import fr.uga.iut2.genevent.modele.Amphitheatre;
import fr.uga.iut2.genevent.modele.Batiment;
import fr.uga.iut2.genevent.modele.EspaceReunion;
import fr.uga.iut2.genevent.modele.Salle;
import fr.uga.iut2.genevent.modele.Conference;
import fr.uga.iut2.genevent.modele.Session;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;

import java.util.HashMap;

/**
 * Contrôleur du formulaire d'ajout et de modification d'un espace de réunion.
 */
public class EspaceReunionController {

    @FXML
    private TextField numeroField;
    @FXML
    private RadioButton typeSalleRadio;
    @FXML
    private RadioButton typeAmphiRadio;
    @FXML
    private TextField capaciteField;
    @FXML
    private TextField videoprojecteursField;
    @FXML
    private TextField ordinateursField;
    @FXML
    private RadioButton sonoOuiRadio;
    @FXML
    private RadioButton sonoNonRadio;
    @FXML
    private TextField microphonesField;
    @FXML
    private TextField tarifField;
    @FXML
    private Button ajouterBtn;
    @FXML
    private TextField nomAmphitheatre;
    @FXML
    private VBox groupeNomAmphitheatre;


    private AppData appData;
    private Batiment batiment;
    private EspaceReunion espaceCourant;

    /**
     * Prépare le formulaire en mode ajout ou modification.
     * @param appData les données de l'application
     * @param batiment le bâtiment auquel rattacher l'espace
     * @param espaceAModifier l'espace à modifier, ou null pour en créer un
     */
    public void init(AppData appData, Batiment batiment, EspaceReunion espaceAModifier) {
        this.appData = appData;
        this.batiment = batiment;
        this.espaceCourant = espaceAModifier;

        if (espaceCourant == null) {
            ajouterBtn.setText("Ajouter l'espace de réunion");
        } else {
            ajouterBtn.setText("Enregistrer");
            numeroField.setText(Integer.toString(espaceCourant.getNumero()));
            capaciteField.setText(Integer.toString(espaceCourant.getCapacitePersonnes()));
            videoprojecteursField.setText(Integer.toString(espaceCourant.getNbVideoProjecteurs()));
            ordinateursField.setText(Integer.toString(espaceCourant.getNbOrdinateurs()));
            microphonesField.setText(Integer.toString(espaceCourant.getNbMicrophones()));
            tarifField.setText(Integer.toString((int) espaceCourant.getTarifHoraire()));

            boolean estAmphi = !espaceCourant.getClass().getSimpleName().equals("Salle");
            typeSalleRadio.setSelected(!estAmphi);
            typeAmphiRadio.setSelected(estAmphi);
            groupeNomAmphitheatre.setDisable(!estAmphi);
            if (estAmphi) {
                nomAmphitheatre.setText(((Amphitheatre) espaceCourant).getNom());
            }
            sonoOuiRadio.setSelected(espaceCourant.isSystemSonorisationPresent());
            sonoNonRadio.setSelected(!espaceCourant.isSystemSonorisationPresent());
        }
    }

    /**
     * Configure les boutons radio et les contraintes de saisie au chargement.
     */
    @FXML
    private void initialize() {
        typeSalleRadio.setOnAction(e -> {
            groupeNomAmphitheatre.setDisable(true);
            nomAmphitheatre.setText("");
        });

        typeAmphiRadio.setOnAction(e -> {
            groupeNomAmphitheatre.setDisable(false);
        });

        TextField[] champs = {numeroField, capaciteField, videoprojecteursField, ordinateursField, microphonesField, tarifField};
        for (TextField champ : champs) {
            Util.forceNumericValue(champ);
            Util.validerObligatoire(champ);
        }
    }

    /**
     * Crée ou modifie l'espace après vérification des champs et du numéro unique.
     * @param event l'événement du clic
     */
    @FXML
    private void actionButtonAddEspace(ActionEvent event) {
        TextInputControl[] champs = {numeroField, capaciteField, videoprojecteursField, ordinateursField, microphonesField, tarifField};
        if (Validation.champsRemplis("Veuillez remplir tous les champs de l'espace de réunion.", champs)) {
            try {
                int numero = Integer.parseInt(numeroField.getText().trim());

                if (numeroDuplique(numero)) {
                    String msg = String.format("Un espace avec le numéro %d existe déjà dans ce bâtiment.", numero);
                    Validation.alerte(msg);
                    return;
                }

                int capacite = Integer.parseInt(capaciteField.getText().trim());
                int videoprojecteurs = Integer.parseInt(videoprojecteursField.getText().trim());
                int ordinateurs = Integer.parseInt(ordinateursField.getText().trim());
                int microphones = Integer.parseInt(microphonesField.getText().trim());
                int tarif = Integer.parseInt(tarifField.getText().trim());
                String nomAmphitheatreText = typeAmphiRadio.isSelected() ? nomAmphitheatre.getText() : "";

                boolean sonorisation = sonoOuiRadio.isSelected();

                EspaceReunion espace;
                if (typeSalleRadio.isSelected()) {
                    espace = new Salle(numero, capacite, videoprojecteurs, ordinateurs, sonorisation, tarif, microphones);
                } else {
                    espace = new Amphitheatre(numero, capacite, videoprojecteurs, ordinateurs, sonorisation, tarif, microphones, nomAmphitheatreText);
                }

                if (espaceCourant == null) {
                    batiment.addEspace(espace);
                } else {
                    espace.setAppartientA(batiment);
                    int index = batiment.getListeEspaces().indexOf(espaceCourant);
                    batiment.getListeEspaces().set(index, espace);

                    for (Conference conference : appData.getConferences()) {
                        for (Session session : conference.getSessions()) {
                            if (session.getOccupe() == espaceCourant) {
                                session.setOccupe(espace);
                            }
                        }
                    }
                }
                Persisteur.sauvegarder(appData);
                Fenetres.fermer(event);
            } catch (NumberFormatException e) {
                Validation.alerte("Les nombres saisis ne sont pas valides.");
            }
        }
    }

    /**
     * Dit si un autre espace du bâtiment porte déjà ce numéro.
     * @param numero le numéro à vérifier
     * @return true si le numéro est déjà utilisé
     */
    private boolean numeroDuplique(int numero) {
        for (EspaceReunion espace : batiment.getListeEspaces()) {
            if (espace != espaceCourant && espace.getNumero() == numero) {
                return true;
            }
        }
        return false;
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
