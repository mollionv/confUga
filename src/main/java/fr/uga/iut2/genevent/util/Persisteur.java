package fr.uga.iut2.genevent.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import fr.uga.iut2.genevent.modele.AppData;

public final class Persisteur {

    private static final String NOM_BDD = "persistence/uconf.bdd";

    private Persisteur() {
        throw new IllegalStateException("Classe utilitaire.");
    }

    public static void sauverEtat(final AppData appData) throws FileNotFoundException, IOException {
        try (
            FileOutputStream fos = new FileOutputStream(Persisteur.NOM_BDD);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(appData);
        }
    }

    public static void sauvegarder(final AppData appData) {
        try {
            sauverEtat(appData);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    public static AppData lireEtat() throws ClassNotFoundException, IOException {
        AppData appData;
        try (
            FileInputStream fis = new FileInputStream(Persisteur.NOM_BDD);
            ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            appData = (AppData) ois.readObject();
        }
        catch (FileNotFoundException ignored) {
            appData = new AppData();
        }
        return appData;
    }
}
