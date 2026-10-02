package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.AppData;
import fr.uga.iut2.genevent.modele.Batiment;
import fr.uga.iut2.genevent.modele.Conference;
import fr.uga.iut2.genevent.modele.EspaceReunion;
import fr.uga.iut2.genevent.modele.Session;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur du formulaire d'ajout, de modification et de suppression d'une session.
 */
public class SessionController {

    @FXML
    private TextField titreSessionField;
    @FXML
    private TextField intervenantsSessionField;
    @FXML
    private ComboBox<Batiment> selectBatimentSession;
    @FXML
    private ComboBox<EspaceReunion> selectEspaceReunionSession;
    @FXML
    private DatePicker dateSession;
    @FXML
    private ComboBox<String> selectHeureDebut;
    @FXML
    private ComboBox<String> selectMinuteDebut;
    @FXML
    private ComboBox<String> selectHeureFin;
    @FXML
    private ComboBox<String> selectMinuteFin;
    @FXML
    private Label nomSession;
    @FXML
    private Button ajouterSessionBtn;
    @FXML
    private Button supprimerSessionBtn;

    private AppData appData;
    private Conference conference;
    private Session sessionAModifier;

    /**
     * Prépare le formulaire en mode création de session.
     * @param appData les données de l'application
     * @param conference la conférence à laquelle ajouter la session
     */
    public void init(AppData appData, Conference conference) {
        init(appData, conference, null);
    }

    /**
     * Prépare le formulaire en mode ajout ou modification.
     * @param appData les données de l'application
     * @param conference la conférence concernée
     * @param sessionAModifier la session à modifier, ou null pour en créer une
     */
    public void init(AppData appData, Conference conference, Session sessionAModifier) {
        this.appData = appData;
        this.conference = conference;
        this.sessionAModifier = sessionAModifier;

        selectBatimentSession.getItems().setAll(appData.getBatiments());

        if (sessionAModifier == null) {
            remplirComboEspaces();
        } else {
            nomSession.setText("Modifier la session");
            ajouterSessionBtn.setText("Enregistrer les modifications");
            supprimerSessionBtn.setVisible(true);
            supprimerSessionBtn.setManaged(true);
            preremplir(sessionAModifier);
        }
    }

    /**
     * Remplit les listes d'heures et de minutes et configure les champs au chargement.
     */
    @FXML
    private void initialize() {
        for (int heure = 0; heure < 24; heure++) {
            selectHeureDebut.getItems().add(String.format("%02d", heure));
            selectHeureFin.getItems().add(String.format("%02d", heure));
        }
        for (int minute = 0; minute < 60; minute += 15) {
            selectMinuteDebut.getItems().add(String.format("%02d", minute));
            selectMinuteFin.getItems().add(String.format("%02d", minute));
        }

        selectMinuteDebut.setEditable(true);
        selectMinuteFin.setEditable(true);
        Util.limitLength(selectMinuteDebut.getEditor(), 2);
        Util.limitLength(selectMinuteFin.getEditor(), 2);
        selectMinuteDebut.getEditor().textProperty().addListener((obs, avant, apres) -> actionHoraire());
        selectMinuteFin.getEditor().textProperty().addListener((obs, avant, apres) -> actionHoraire());

        Util.validerObligatoire(titreSessionField);
        Util.validerObligatoire(intervenantsSessionField);
    }

    /**
     * Remplit le formulaire avec les valeurs d'une session existante.
     * @param session la session à pré-remplir
     */
    private void preremplir(Session session) {
        titreSessionField.setText(session.getNom());
        intervenantsSessionField.setText(String.join(", ", session.getIntervenants()));
        dateSession.setValue(session.getDateReservation());
        selectHeureDebut.setValue(String.format("%02d", session.getHeureDebut().getHour()));
        selectMinuteDebut.setValue(String.format("%02d", session.getHeureDebut().getMinute()));
        selectHeureFin.setValue(String.format("%02d", session.getHeureFin().getHour()));
        selectMinuteFin.setValue(String.format("%02d", session.getHeureFin().getMinute()));

        EspaceReunion espace = session.getOccupe();
        if (espace != null) {
            selectBatimentSession.setValue(espace.getAppartientA());
        }
        remplirComboEspaces();
        if (espace != null) {
            selectEspaceReunionSession.setValue(espace);
        }
    }

    /**
     * Lit l'heure de début choisie dans les listes.
     * @return l'heure de début, ou null si la saisie est invalide
     */
    private LocalTime heureDebut() {
        return construireHeure(selectHeureDebut.getValue(), selectMinuteDebut.getEditor().getText());
    }

    /**
     * Lit l'heure de fin choisie dans les listes.
     * @return l'heure de fin, ou null si la saisie est invalide
     */
    private LocalTime heureFin() {
        return construireHeure(selectHeureFin.getValue(), selectMinuteFin.getEditor().getText());
    }

    /**
     * Construit une heure à partir des textes d'heure et de minute.
     * @param texteHeure le texte des heures
     * @param texteMinute le texte des minutes
     * @return l'heure construite, ou null si les valeurs sont invalides
     */
    private LocalTime construireHeure(String texteHeure, String texteMinute) {
        LocalTime heure = null;
        Integer h = parseEntier(texteHeure);
        Integer m = parseEntier(texteMinute);
        if (h != null && m != null && h <= 23 && m <= 59) {
            heure = LocalTime.of(h, m);
        }
        return heure;
    }

    /**
     * Convertit un texte en entier s'il ne contient que des chiffres.
     * @param texte le texte à convertir
     * @return l'entier, ou null si le texte n'est pas un nombre
     */
    private Integer parseEntier(String texte) {
        Integer valeur = null;
        if (texte != null && texte.trim().matches("\\d+")) {
            valeur = Integer.parseInt(texte.trim());
        }
        return valeur;
    }

    /**
     * Remplit la liste des espaces du bâtiment, en retirant ceux déjà réservés sur le créneau.
     */
    private void remplirComboEspaces() {
        EspaceReunion selectionCourante = selectEspaceReunionSession.getValue();
        selectEspaceReunionSession.getItems().clear();
        Batiment batiment = selectBatimentSession.getValue();
        selectEspaceReunionSession.setDisable(batiment == null);
        if (batiment != null) {
            LocalDate date = dateSession.getValue();
            LocalTime debut = heureDebut();
            LocalTime fin = heureFin();
            boolean filtrerParCreneau = date != null && debut != null && fin != null && debut.isBefore(fin);

            for (EspaceReunion espace : batiment.getListeEspaces()) {
                boolean disponible = !filtrerParCreneau || appData.estDisponible(espace, date, debut, fin, sessionAModifier, conference);
                if (disponible) {
                    selectEspaceReunionSession.getItems().add(espace);
                }
            }

            if (selectionCourante != null && selectEspaceReunionSession.getItems().contains(selectionCourante)) {
                selectEspaceReunionSession.setValue(selectionCourante);
            }
        }
    }

    /**
     * Met à jour la liste des espaces quand on change de bâtiment.
     */
    @FXML
    private void actionSelectBatiment() {
        remplirComboEspaces();
    }

    /**
     * Met à jour la liste des espaces quand on change de date.
     */
    @FXML
    private void actionSelectDate() {
        remplirComboEspaces();
    }

    /**
     * Met à jour la liste des espaces et la validité quand on change d'horaire.
     */
    @FXML
    private void actionHoraire() {
        remplirComboEspaces();
        majValiditeHoraire();
    }

    /**
     * Colore les listes d'horaires en rouge si le début n'est pas avant la fin.
     */
    private void majValiditeHoraire() {
        LocalTime debut = heureDebut();
        LocalTime fin = heureFin();
        boolean erreur = debut != null && fin != null && !debut.isBefore(fin);
        majRouge(selectHeureDebut, erreur);
        majRouge(selectMinuteDebut, erreur);
        majRouge(selectHeureFin, erreur);
        majRouge(selectMinuteFin, erreur);
    }

    /**
     * Ajoute ou enlève le style d'erreur sur une liste déroulante.
     * @param combo la liste concernée
     * @param erreur true pour marquer en erreur
     */
    private void majRouge(ComboBox<String> combo, boolean erreur) {
        combo.getStyleClass().remove("champ-invalide");
        if (erreur) {
            combo.getStyleClass().add("champ-invalide");
        }
    }

    /**
     * Vérifie le formulaire, contrôle les conflits puis enregistre la session.
     * @param event l'événement du clic
     */
    @FXML
    private void actionButtonAddSession(ActionEvent event) {
        EspaceReunion espace = selectEspaceReunionSession.getValue();
        LocalDate date = dateSession.getValue();
        LocalTime debut = heureDebut();
        LocalTime fin = heureFin();

        if (Validation.estVide(titreSessionField) || Validation.estVide(intervenantsSessionField)) {
            Validation.alerte("Veuillez renseigner le titre et le ou les intervenant(s).");
        } else if (espace == null || date == null) {
            Validation.alerte("Veuillez choisir un espace de réunion et une date.");
        } else if (debut == null || fin == null) {
            Validation.alerte("Veuillez choisir l'heure de début et l'heure de fin.");
        } else if (!debut.isBefore(fin)) {
            Validation.alerte("L'heure de début doit être avant l'heure de fin.");
        } else {
            List<Session> conflits = appData.sessionsEnConflit(espace, date, debut, fin, sessionAModifier, conference);
            if (!conflits.isEmpty()) {
                Session conflit = conflits.get(0);
                String heureDebut = conflit.getHeureDebut().format(Formats.HEURE);
                String heureFin = conflit.getHeureFin().format(Formats.HEURE);
                String msg = String.format("L'espace « %s » est déjà réservé le %s de %s à %s (session « %s »).\nChoisissez un autre créneau ou un autre espace.",
                    espace, date.format(Formats.DATE), heureDebut, heureFin, conflit.getNom());
                Validation.alerte(msg);
            } else {
                ArrayList<String> intervenants = new ArrayList<>();
                for (String intervenant : intervenantsSessionField.getText().split(",")) {
                    if (!intervenant.trim().isEmpty()) {
                        intervenants.add(intervenant.trim());
                    }
                }

                Session session = new Session(titreSessionField.getText().trim(), date, debut, fin, intervenants, espace, conference);
                if (sessionAModifier != null) {
                    conference.removeSession(sessionAModifier);
                }
                conference.addSession(session);
                Persisteur.sauvegarder(appData);
                Fenetres.fermer(event);
            }
        }
    }

    /**
     * Supprime la session en cours de modification après confirmation.
     * @param event l'événement du clic
     */
    @FXML
    private void actionButtonSupprimer(ActionEvent event) {
        if (sessionAModifier != null) {
            String msg = String.format("Supprimer définitivement la session « %s » ?", sessionAModifier.getNom());
            if (Validation.confirmer(msg)) {
                conference.removeSession(sessionAModifier);
                Persisteur.sauvegarder(appData);
                Fenetres.fermer(event);
            }
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
