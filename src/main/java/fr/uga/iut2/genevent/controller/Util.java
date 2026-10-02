package fr.uga.iut2.genevent.controller;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

/**
 * Méthodes utilitaires pour contrôler la saisie dans les champs texte.
 */
public class Util {

	/**
	 * Empêche de taper autre chose que des chiffres dans le champ.
	 * @param numericField le champ à restreindre
	 */
	public static void forceNumericValue(TextField numericField) {
		numericField.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().matches("\\d*")) {
				return change;
			}
			return null;
		}));
	}

	/**
	 * Limite le champ à un nombre maximal de chiffres.
	 * @param field le champ à limiter
	 * @param max le nombre de caractères autorisés
	 */
	public static void limitLength(TextField field, int max) {
		field.setTextFormatter(new TextFormatter<>(change -> {
			String newText = change.getControlNewText();

			if (newText.matches("\\d*") && newText.length() <= max) {
				return change;
			}
			return null;
		}));
	}

	/**
	 * Colore le champ en rouge quand il est laissé vide.
	 * @param champ le champ obligatoire à surveiller
	 */
	public static void validerObligatoire(TextInputControl champ) {
		champ.focusedProperty().addListener((obs, avant, apres) -> {
			if (!apres) {
				majValidite(champ, Validation.estVide(champ));
			}
		});
		champ.textProperty().addListener((obs, avant, apres) -> {
			if (champ.getStyleClass().contains("champ-invalide")) {
				majValidite(champ, Validation.estVide(champ));
			}
		});
	}

	/**
	 * Ajoute ou enlève le style d'erreur sur un champ.
	 * @param champ le champ concerné
	 * @param enErreur true pour marquer le champ en erreur
	 */
	private static void majValidite(TextInputControl champ, boolean enErreur) {
		champ.getStyleClass().remove("champ-invalide");
		if (enErreur) {
			champ.getStyleClass().add("champ-invalide");
		}
	}
}
