package fr.uga.iut2.genevent.controller;

import fr.uga.iut2.genevent.modele.AppData;
import fr.uga.iut2.genevent.modele.Conference;
import fr.uga.iut2.genevent.modele.EspaceReunion;
import fr.uga.iut2.genevent.modele.Session;
import fr.uga.iut2.genevent.util.Persisteur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur de la fenêtre de détails d'une conférence (vue journée et vue semaine).
 */
public class DetailsConferenceController {

    private static final double HAUTEUR_HEURE = 80.0;
    private static final int JOURS_VISIBLES = 3;

    @FXML
    private Label nomConferenceLabel;
    @FXML
    private Label datePeriodeLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label prixLabel;
    @FXML
    private Label programmeTitreLabel;

    @FXML
    private VBox vueJournee;
    @FXML
    private GridPane enteteJournee;
    @FXML
    private ScrollPane scrollJournee;
    @FXML
    private GridPane grilleJournee;
    @FXML
    private Button flecheHautJournee;
    @FXML
    private Button flecheBasJournee;

    @FXML
    private VBox vueSemaine;
    @FXML
    private GridPane enteteSemaine;
    @FXML
    private ScrollPane scrollSemaine;
    @FXML
    private GridPane grilleSemaine;
    @FXML
    private Button flecheHautSemaine;
    @FXML
    private Button flecheBasSemaine;
    @FXML
    private Button flecheGaucheSemaine;
    @FXML
    private Button flecheDroiteSemaine;
    @FXML
    private Label messageAucuneSessionLabel;

    private AppData appData;
    private Conference conference;
    private int offsetJourSemaine;
    private boolean cadrageInitial;

    /**
     * Initialise la fenêtre avec la conférence à afficher.
     * @param appData les données de l'application
     * @param conference la conférence à afficher
     */
    public void init(AppData appData, Conference conference) {
        this.appData = appData;
        this.conference = conference;
        this.offsetJourSemaine = 0;
        this.cadrageInitial = true;
        configurerDefilement();
        refresh();
    }

    /**
     * Branche les flèches de défilement sur les changements de position des scrolls.
     */
    private void configurerDefilement() {
        scrollJournee.vvalueProperty().addListener((obs, ancien, nouveau) -> majFlechesJournee());
        scrollJournee.viewportBoundsProperty().addListener((obs, ancien, nouveau) -> majFlechesJournee());
        scrollSemaine.vvalueProperty().addListener((obs, ancien, nouveau) -> majFlechesSemaine());
        scrollSemaine.viewportBoundsProperty().addListener((obs, ancien, nouveau) -> majFlechesSemaine());
    }

    /**
     * Met à jour l'en-tête et choisit la vue journée ou la vue semaine.
     */
    private void refresh() {
        nomConferenceLabel.setText(conference.getNomConference());
        descriptionLabel.setText(conference.getDescription());
        prixLabel.setText(String.format("%.2f€", conference.calculerPrix()));

        LocalDate debut = conference.getDebut();
        LocalDate fin = conference.getFin();
        if (debut == null) {
            datePeriodeLabel.setText("Aucune session programmée");
            messageAucuneSessionLabel.setVisible(true);
            messageAucuneSessionLabel.setManaged(true);
        } else {
            datePeriodeLabel.setText(debut.format(Formats.DATE) + " - " + fin.format(Formats.DATE));
            messageAucuneSessionLabel.setVisible(false);
            messageAucuneSessionLabel.setManaged(false);
        }

        boolean uneSeuleJournee = (debut == null) || debut.equals(fin);
        vueJournee.setVisible(uneSeuleJournee);
        vueJournee.setManaged(uneSeuleJournee);
        vueSemaine.setVisible(!uneSeuleJournee);
        vueSemaine.setManaged(!uneSeuleJournee);

        if (uneSeuleJournee) {
            programmeTitreLabel.setText("PROGRAMME DE LA JOURNÉE");
            refreshVueJournee();
        } else {
            programmeTitreLabel.setText("PROGRAMME DE LA SEMAINE");
            offsetJourSemaine = 0;
            afficherJoursSemaine();
        }
    }

    /**
     * Affiche la grille d'une seule journée.
     */
    private void refreshVueJournee() {
        List<LocalDate> jours = new ArrayList<>();
        jours.add(conference.getDebut());
        construireGrille(enteteJournee, grilleJournee, jours);
        Platform.runLater(() -> majFlechesJournee());
        if (cadrageInitial) {
            cadrerSurPremiereSession(scrollJournee, grilleJournee);
            cadrageInitial = false;
        }
    }

    /**
     * Affiche la grille des jours visibles de la semaine selon le décalage courant.
     */
    private void afficherJoursSemaine() {
        LocalDate debutConf = conference.getDebut();
        LocalDate finConf = conference.getFin();

        List<LocalDate> jours = new ArrayList<>();
        int i = 0;

        while (i < JOURS_VISIBLES && debutConf.plusDays(offsetJourSemaine + i - 1).isBefore(finConf)) {
            jours.add(debutConf.plusDays(offsetJourSemaine + i));
            i++;
        }

        construireGrille(enteteSemaine, grilleSemaine, jours);
        majFlechesJours();
        Platform.runLater(() -> majFlechesSemaine());
        if (cadrageInitial) {
            cadrerSurPremiereSession(scrollSemaine, grilleSemaine);
            cadrageInitial = false;
        }
    }

    /**
     * Cherche la session la plus tôt (date puis heure).
     * @return la première session, ou null s'il n'y en a aucune
     */
    private Session premiereSession() {
        Session premiere = null;
        for (Session session : conference.getSessions()) {
            if (premiere == null || session.getDateReservation().isBefore(premiere.getDateReservation()) || (session.getDateReservation().equals(premiere.getDateReservation()) && session.getHeureDebut().isBefore(premiere.getHeureDebut()))) {
                premiere = session;
            }
        }
        return premiere;
    }

    /**
     * Fait défiler la grille pour montrer la première session au démarrage.
     * @param scroll le conteneur défilant
     * @param grille la grille des sessions
     */
    private void cadrerSurPremiereSession(ScrollPane scroll, GridPane grille) {
        Session premiere = premiereSession();
        if (premiere != null) {
            int decalage = premiere.getHeureDebut().getHour() - calculerBornesHeures()[0];
            double y = decalage * (HAUTEUR_HEURE + grille.getVgap());
            Platform.runLater(() -> {
                double defilable = grille.getHeight() - scroll.getViewportBounds().getHeight();
                if (defilable > 0.0) {
                    scroll.setVvalue(Math.max(0.0, Math.min(1.0, y / defilable)));
                }
            });
        }
    }

    /**
     * Construit l'en-tête (jours) et la grille des sessions placées par heure.
     * @param entete la grille d'en-tête des jours
     * @param grille la grille des sessions
     * @param jours les jours à afficher en colonnes
     */
    private void construireGrille(GridPane entete, GridPane grille, List<LocalDate> jours) {
        entete.getChildren().clear();
        grille.getChildren().clear();
        grille.getRowConstraints().clear();

        int[] bornes = calculerBornesHeures();
        int premiereHeure = bornes[0];
        int derniereHeure = bornes[1];
        int nbHeures = derniereHeure - premiereHeure + 1;

        for (int i = 0; i < nbHeures; i++) {
            RowConstraints ligne = new RowConstraints();
            ligne.setMinHeight(HAUTEUR_HEURE);
            ligne.setPrefHeight(HAUTEUR_HEURE);
            ligne.setMaxHeight(HAUTEUR_HEURE);
            grille.getRowConstraints().add(ligne);
        }

        for (int i = 0; i < jours.size(); i++) {
            LocalDate jour = jours.get(i);
            Label labelJour = new Label();
            if (jour != null) {
                labelJour.setText(Formats.jour(jour));
            }
            labelJour.setMaxWidth(Double.MAX_VALUE);
            labelJour.setAlignment(Pos.CENTER);
            GridPane.setColumnIndex(labelJour, i + 1);
            GridPane.setRowIndex(labelJour, 0);
            entete.getChildren().add(labelJour);
        }
        boolean aucuneSession = conference.getSessions().isEmpty();
        if (!aucuneSession) {
            for (int ligne = 0; ligne < nbHeures; ligne++) {
                Label labelHeure = new Label((premiereHeure + ligne) + "h");
                GridPane.setColumnIndex(labelHeure, 0);
                GridPane.setRowIndex(labelHeure, ligne);
                GridPane.setValignment(labelHeure, VPos.TOP);
                grille.getChildren().add(labelHeure);
            }
        }
        messageAucuneSessionLabel.setVisible(aucuneSession);
        messageAucuneSessionLabel.setManaged(aucuneSession);

        for (int colonne = 0; colonne < jours.size(); colonne++) {
            LocalDate jour = jours.get(colonne);
            if (jour != null) {
                ArrayList<Session> sessionsDuJour = new ArrayList<>();
                for (Session session : conference.getSessions()) {
                    if (session.getDateReservation().equals(jour)) {
                        sessionsDuJour.add(session);
                    }
                }
                sessionsDuJour.sort((a, b) -> a.getHeureDebut().compareTo(b.getHeureDebut()));

                int i = 0;
                while (i < sessionsDuJour.size()) {
                    ArrayList<Session> groupe = new ArrayList<>();
                    groupe.add(sessionsDuJour.get(i));
                    int debutGroupe = sessionsDuJour.get(i).getHeureDebut().getHour();
                    int finGroupe = heureFinExclusive(sessionsDuJour.get(i));

                    int j = i + 1;
                    while (j < sessionsDuJour.size() && sessionsDuJour.get(j).getHeureDebut().getHour() < finGroupe) {
                        groupe.add(sessionsDuJour.get(j));
                        finGroupe = Math.max(finGroupe, heureFinExclusive(sessionsDuJour.get(j)));
                        j++;
                    }

                    HBox cellule = new HBox(8.0);
                    cellule.setAlignment(Pos.TOP_LEFT);
                    cellule.setMinWidth(0.0);
                    for (Session session : groupe) {
                        VBox carte = creerCarteSession(session);
                        carte.setMaxHeight(Double.MAX_VALUE);
                        HBox.setHgrow(carte, Priority.ALWAYS);
                        cellule.getChildren().add(carte);
                    }

                    GridPane.setColumnIndex(cellule, colonne + 1);
                    GridPane.setRowIndex(cellule, debutGroupe - premiereHeure);
                    GridPane.setRowSpan(cellule, finGroupe - debutGroupe);
                    GridPane.setVgrow(cellule, Priority.ALWAYS);
                    grille.getChildren().add(cellule);

                    i = j;
                }
            }
        }
    }

    /**
     * Donne l'heure de fin arrondie au-dessus (au moins une heure après le début).
     * @param session la session concernée
     * @return l'heure de fin exclusive en heures pleines
     */
    private int heureFinExclusive(Session session) {
        int debut = session.getHeureDebut().getHour();
        int minutesFin = session.getHeureFin().getHour() * 60 + session.getHeureFin().getMinute();
        int fin = (int) Math.ceil(minutesFin / 60.0);
        if (fin <= debut) {
            fin = debut + 1;
        }
        return fin;
    }

    /**
     * Crée la carte cliquable affichant les infos d'une session.
     * @param session la session à afficher
     * @return la carte de la session
     */
    private VBox creerCarteSession(Session session) {
        VBox carte = new VBox(4.0);
        carte.getStyleClass().add("card");
        carte.setMinWidth(0.0);
        carte.setCursor(Cursor.HAND);
        carte.setOnMouseClicked(event -> ouvrirSession(session));

        EspaceReunion espace = session.getOccupe();
        String batimentNom = "-";
        String espaceStr = "-";
        if (espace != null) {
            espaceStr = espace.toString();
            if (espace.getAppartientA() != null) {
                batimentNom = espace.getAppartientA().getNomBatiment();
            }
        }

        StringBuilder intervenants = new StringBuilder();
        List<String> liste = session.getIntervenants();
        for (int i = 0; i < liste.size(); i++) {
            if (i > 0) {
                intervenants.append(", ");
            }
            intervenants.append(liste.get(i));
        }

        String heureDebut = session.getHeureDebut().format(Formats.HEURE);
        String heureFin = session.getHeureFin().format(Formats.HEURE);
        String ligneInfo = String.format("%s - %s - %s - %s - %s", heureDebut, heureFin, espaceStr, batimentNom, intervenants);
        Label infoLabel = new Label(ligneInfo);
        infoLabel.setWrapText(true);

        Label nomLabel = new Label(session.getNom());
        nomLabel.setWrapText(true);

        carte.getChildren().addAll(new Label("SESSION"), nomLabel, infoLabel);
        return carte;
    }

    /**
     * Calcule la première et la dernière heure occupées par les sessions.
     * @return un tableau {heureMin, heureMax} (8h par défaut si aucune session)
     */
    private int[] calculerBornesHeures() {
        int heureMin = 23;
        int heureMax = 0;
        boolean auMoinsUneSession = false;
        for (Session session : conference.getSessions()) {
            auMoinsUneSession = true;
            int debut = session.getHeureDebut().getHour();
            int fin = heureFinExclusive(session) - 1;
            if (debut < heureMin) {
                heureMin = debut;
            }
            if (fin > heureMax) {
                heureMax = fin;
            }
        }
        if (!auMoinsUneSession) {
            heureMin = 8;
            heureMax = 8;
        }
        return new int[]{heureMin, heureMax};
    }

    /**
     * Met à jour les flèches verticales de la vue journée.
     */
    private void majFlechesJournee() {
        majFlechesVerticales(scrollJournee, grilleJournee, flecheHautJournee, flecheBasJournee);
    }

    /**
     * Met à jour les flèches verticales de la vue semaine.
     */
    private void majFlechesSemaine() {
        majFlechesVerticales(scrollSemaine, grilleSemaine, flecheHautSemaine, flecheBasSemaine);
    }

    /**
     * Affiche ou cache les flèches haut/bas selon la possibilité de défiler.
     * @param scroll le conteneur défilant
     * @param grille la grille affichée
     * @param haut la flèche vers le haut
     * @param bas la flèche vers le bas
     */
    private void majFlechesVerticales(ScrollPane scroll, GridPane grille, Button haut, Button bas) {
        double hauteurVisible = scroll.getViewportBounds().getHeight();
        boolean defilable = grille.getHeight() > hauteurVisible + 1.0;
        double position = scroll.getVvalue();
        haut.setVisible(defilable && position > 0.001);
        bas.setVisible(defilable && position < 0.999);
    }

    /**
     * Fait défiler la grille d'une heure vers le haut ou vers le bas.
     * @param scroll le conteneur défilant
     * @param grille la grille affichée
     * @param sens -1 pour monter, 1 pour descendre
     */
    private void defilerVertical(ScrollPane scroll, GridPane grille, double sens) {
        double hauteurVisible = scroll.getViewportBounds().getHeight();
        double defilable = grille.getHeight() - hauteurVisible;
        if (defilable > 0.0) {
            double pas = (HAUTEUR_HEURE + grille.getVgap()) / defilable;
            double cible = scroll.getVvalue() + sens * pas;
            scroll.setVvalue(Math.max(0.0, Math.min(1.0, cible)));
        }
    }

    /**
     * Affiche ou cache les flèches gauche/droite selon les jours restants.
     */
    private void majFlechesJours() {
        long totalJours = ChronoUnit.DAYS.between(conference.getDebut(), conference.getFin()) + 1;
        flecheGaucheSemaine.setVisible(offsetJourSemaine > 0);
        flecheDroiteSemaine.setVisible(offsetJourSemaine + JOURS_VISIBLES < totalJours);
    }

    /**
     * Ouvre la fenêtre de modification de la conférence.
     */
    @FXML
    private void actionButtonModifierConf() {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/add-conference.fxml");
        AddConferenceController controleur = loader.getController();
        controleur.init(appData, conference);
        Fenetres.afficher(loader, 560, 700, "Modifier la conférence");
        refresh();
    }

    /**
     * Supprime la conférence après confirmation puis ferme la fenêtre.
     * @param event l'événement du clic
     */
    @FXML
    private void actionSupprimerConf(ActionEvent event) {
        if (Validation.confirmer("Supprimer définitivement la conférence « " + conference.getNomConference() + " » ?")) {
            appData.supprimerConference(conference);
            Persisteur.sauvegarder(appData);
            Fenetres.fermer(event);
        }
    }

    /**
     * Ouvre la fenêtre de création d'une nouvelle session.
     */
    @FXML
    private void actionButtonOpenNewSession() {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/session.fxml");
        SessionController controleur = loader.getController();
        controleur.init(appData, conference);
        Fenetres.afficher(loader, 560, 750, "Session");
        refresh();
    }

    /**
     * Ouvre la fenêtre de modification d'une session existante.
     * @param session la session à modifier
     */
    private void ouvrirSession(Session session) {
        FXMLLoader loader = Fenetres.charger("/fr/uga/iut2/genevent/view/session.fxml");
        SessionController controleur = loader.getController();
        controleur.init(appData, conference, session);
        Fenetres.afficher(loader, 560, 750, "Session");
        refresh();
    }

    /**
     * Fait défiler la vue journée vers le haut.
     */
    @FXML
    private void actionDefilerHautJournee() {
        defilerVertical(scrollJournee, grilleJournee, -1.0);
    }

    /**
     * Fait défiler la vue journée vers le bas.
     */
    @FXML
    private void actionDefilerBasJournee() {
        defilerVertical(scrollJournee, grilleJournee, 1.0);
    }

    /**
     * Fait défiler la vue semaine vers le haut.
     */
    @FXML
    private void actionDefilerHautSemaine() {
        defilerVertical(scrollSemaine, grilleSemaine, -1.0);
    }

    /**
     * Fait défiler la vue semaine vers le bas.
     */
    @FXML
    private void actionDefilerBasSemaine() {
        defilerVertical(scrollSemaine, grilleSemaine, 1.0);
    }

    /**
     * Affiche les jours suivants dans la vue semaine.
     */
    @FXML
    private void actionJoursSuivants() {
        long totalJours = ChronoUnit.DAYS.between(conference.getDebut(), conference.getFin()) + 1;
        int maxOffset = (int) Math.max(0, totalJours - JOURS_VISIBLES);
        offsetJourSemaine = Math.min(offsetJourSemaine + 1, maxOffset);
        afficherJoursSemaine();
    }

    /**
     * Affiche les jours précédents dans la vue semaine.
     */
    @FXML
    private void actionJoursPrecedents() {
        offsetJourSemaine = Math.max(0, offsetJourSemaine - 1);
        afficherJoursSemaine();
    }

    /**
     * Demande où enregistrer le calendrier et exporte la conférence en .ics.
     */
    @FXML
    private void actionTelecharger() {
        FileChooser selecteur = new FileChooser();
        selecteur.setTitle("Exporter le calendrier");
        selecteur.setInitialFileName(nomFichierIcs());
        selecteur.getExtensionFilters().add(new FileChooser.ExtensionFilter("Calendrier iCalendar (*.ics)", "*.ics"));

        Window fenetre = prixLabel.getScene().getWindow();
        File fichier = selecteur.showSaveDialog(fenetre);
        if (fichier != null) {
            try {
                Files.writeString(fichier.toPath(), CalendrierIcs.exporter(conference));
                Validation.information("Le calendrier a été téléchargé :\n" + fichier.getName());
            } catch (IOException e) {
                Validation.alerte("Impossible d'enregistrer le fichier : " + e.getMessage());
            }
        }
    }

    /**
     * Construit un nom de fichier .ics à partir du nom de la conférence.
     * @return le nom de fichier proposé
     */
    private String nomFichierIcs() {
        String base = conference.getNomConference().replaceAll("[^a-zA-Z0-9-_]", "_");
        return base + ".ics";
    }

    /**
     * Ferme la fenêtre de détails.
     * @param event l'événement du clic
     */
    @FXML
    private void actionButtonCancel(ActionEvent event) {
        Fenetres.fermer(event);
    }
}
