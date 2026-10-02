package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Une session : un créneau réservé dans un espace pour une conférence.
 */
public class Session implements Serializable {

	private static final long serialVersionUID = 1L;
	private String nom;
	private LocalDate dateReservation;
	private LocalTime heureDebut;
	private LocalTime heureFin;
	private final ArrayList<String> intervenants;
	private EspaceReunion occupe;
	private Conference appartientA;

	/**
	 * Construit une nouvelle session avec sa liste d'intervenants.
	 * @param nom nom de la session
	 * @param dateReservation date de la session
	 * @param heureDebut heure de début
	 * @param heureFin heure de fin
	 * @param listeIntervenants liste des intervenants (peut être null)
	 * @param occupe l'espace de réunion réservé
	 * @param appartientA la conférence à laquelle appartient la session
	 */
	public Session(String nom, LocalDate dateReservation, LocalTime heureDebut, LocalTime heureFin, ArrayList<String> listeIntervenants, EspaceReunion occupe, Conference appartientA) {
		setNom(nom);
		setDateReservation(dateReservation);
		setHeureDebut(heureDebut);
		setHeureFin(heureFin);
		this.intervenants = new ArrayList<>();
		if (listeIntervenants != null) {
			this.intervenants.addAll(listeIntervenants);
		}
		this.occupe = occupe;
		this.appartientA = appartientA;
	}

	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public LocalDate getDateReservation() {
		return this.dateReservation;
	}

	public void setDateReservation(LocalDate dateReservation) {
		this.dateReservation = dateReservation;
	}

	public LocalTime getHeureDebut() {
		return this.heureDebut;
	}

	public void setHeureDebut(LocalTime heureDebut) {
		this.heureDebut = heureDebut;
	}

	public LocalTime getHeureFin() {
		return this.heureFin;
	}

	public void setHeureFin(LocalTime heureFin) {
		this.heureFin = heureFin;
	}

	public List<String> getIntervenants() {
		return this.intervenants;
	}

	/**
	 * Ajoute un intervenant à la session.
	 * @param intervenant le nom de l'intervenant à ajouter
	 */
	public void addIntervenant(String intervenant) {
		this.intervenants.add(intervenant);
	}

	/**
	 * Retire un intervenant de la session.
	 * @param intervenant le nom de l'intervenant à retirer
	 */
	public void removeIntervenant(String intervenant) {
		this.intervenants.remove(intervenant);
	}

	public EspaceReunion getOccupe() {
		return occupe;
	}

	public void setOccupe(EspaceReunion occupe) {
		this.occupe = occupe;
	}

	public Conference getAppartientA() {
		return appartientA;
	}

	/**
	 * Calcule la durée de la session à partir de ses heures de début et de fin.
	 * @return la durée en heures, ou 0 si une des heures est absente
	 */
	public double getDureeHeures() {
		if (heureDebut == null || heureFin == null) {
			return 0;
		}
		return Duration.between(heureDebut, heureFin).toMinutes() / 60.0;
	}

	/**
	 * Calcule le prix de la session selon le tarif de l'espace et sa durée.
	 * @return le prix de la session, ou 0 si aucun espace n'est réservé
	 */
	public double calculerPrix() {
		if (occupe == null) {
			return 0;
		}
		return occupe.calculerPrix(getDureeHeures());
	}
}
