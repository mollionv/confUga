package fr.uga.iut2.genevent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fr/uga/iut2/genevent/view/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 890, 600);
        scene.getStylesheets().add(
                Main.class.getResource("/fr/uga/iut2/genevent/view/style.css").toExternalForm()
        );

        stage.setTitle("Accueil - uConf");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("/fr/uga/iut2/genevent/view/icon.png")));
        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}