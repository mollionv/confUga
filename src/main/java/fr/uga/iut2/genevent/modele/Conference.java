package fr.uga.iut2.genevent.modele;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Une conférence : un ensemble de sessions réservé par une personne.
 */
public class Conference implements Serializable {

    private static final long serialVersionUID = 1L;
	private int numConference;
	private String nomConference;
	private String descriptionConference;
	private String nomReserveur;
	private ArrayList<Session> sessions;

	/**
	 * Construit une nouvelle conférence, sans aucune session au départ.
	 * @param numConf numéro de la conférence
	 * @param nomConf nom de la conférence
	 * @param descriptionConf description de la conférence
	 * @param nomReserveur nom de la personne qui réserve
	 */
	public Conference(int numConf, String nomConf, String descriptionConf, String nomReserveur) {
		this.numConference = numConf;
		this.nomConference = nomConf;
		this.descriptionConference = descriptionConf;
		this.nomReserveur = nomReserveur;
		this.sessions = new ArrayList<>();
	}

	public String getNomConference() {
		return nomConference;
	}

	public void setNomConference(String nomConference) {
		this.nomConference = nomConference;
	}

	public String getDescription() {
		return descriptionConference;
	}

	public void setDescription(String descriptionConference) {
		this.descriptionConference = descriptionConference;
	}

	public int getNumConference() {
		return numConference;
	}

	public void setNumConference(int numConference) {
		this.numConference = numConference;
	}

	/**
	 * Ajoute une session à la conférence.
	 * @param sess la session à ajouter
	 */
	public void addSession(Session sess) {
		sessions.add(sess);
	}

	/**
	 * Retire une session de la conférence.
	 * @param sess la session à retirer
	 */
	public void removeSession(Session sess) {
		sessions.remove(sess);
	}

	/**
	 * Ajoute plusieurs sessions d'un coup à la conférence.
	 * @param listSess la liste de sessions à ajouter
	 */
	public void addListSession(ArrayList<Session> listSess) {
        sessions.addAll(listSess);
	}

	public String getNomReserveur() {
		return nomReserveur;
	}

	public void setNomReserveur(String nomReserveur) {
		this.nomReserveur = nomReserveur;
	}

	/**
	 * Construit le programme de la conférence : pour chaque session,
	 * son nom, sa date, ses heures de début et de fin et ses intervenants.
	 * @return le programme sous forme de texte, une session par ligne
	 */
	public String getProgramme() {
		StringBuilder programme_string = new StringBuilder();

		for (Session session : sessions) {
			StringBuilder intervenants = new StringBuilder();

			for (String intervenant : session.getIntervenants()) {
				intervenants.append(intervenant).append(" ");
			}

			programme_string.append(session.getNom()).append(" - Date: ").append(session.getDateReservation()).append(" - Heure début: ").append(session.getHeureDebut()).append(" - Heure fin: ").append(session.getHeureFin()).append(" - Intervenant(s): ").append(intervenants).append("\n");
		}

		return programme_string.toString();
	}

	public ArrayList<Session> getSessions() {
		return sessions;
	}

	/**
	 * Calcule le prix total de la conférence en additionnant
	 * le prix de chacune de ses sessions.
	 * @return le prix total de la conférence
	 */
	public double calculerPrix() {
		double total = 0;
		for (Session session : sessions) {
			total += session.calculerPrix();
		}
		return total;
	}

	/**
	 * Parcourt les sessions pour trouver la date la plus tôt,
	 * c'est-à-dire la date de début de la conférence.
	 * @return la date de début, ou null si la conférence n'a aucune session
	 */
	public LocalDate getDebut() {
		if (!sessions.isEmpty()) {
			LocalDate dateMin = sessions.get(0).getDateReservation();
			for (Session session: sessions) {
				if (session.getDateReservation().isBefore(dateMin)) {
					dateMin = session.getDateReservation();
				}
			}
			return dateMin;
		}
		return null;
	}

	/**
	 * Parcourt les sessions pour trouver la date la plus tard,
	 * c'est-à-dire la date de fin de la conférence.
	 * @return la date de fin, ou null si la conférence n'a aucune session
	 */
	public LocalDate getFin() {
		if (!sessions.isEmpty()) {
			LocalDate dateMax = sessions.get(0).getDateReservation();
			for (Session session: sessions) {
				if (session.getDateReservation().isAfter(dateMax)) {
					dateMax = session.getDateReservation();
				}
			}
			return dateMax;
		} else {
			return null;
		}
    }
}
