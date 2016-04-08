package nl.willemsenmedia.utwente.anonymization.nlp;

/**
 * Created by Martijn on 8-4-2016.
 */
public enum POS {

	VERB("werkwoord", "V", "VB"), NOUN("zelfstandig naamwoord", "N", "NN"), ADJECTIVE("bijvoegelijke naamwoorden", "Adj", "JJ"), PREPOSITION("voorzetsel", "Prep", "IN"), PRONOUN("voornaamwoord", "Pron", "PR"), ADVERB("bijwoord", "Adv", "RB"), UNKNOWN_OTHER("onbekend/overig", "?", "?");

	private String dutch_name;
	private String dutch_acronym;
	private String english_acronym;

	POS(String dutch_name, String dutch_acronym, String english_acronym) {
		this.dutch_name = dutch_name;
		this.dutch_acronym = dutch_acronym;
		this.english_acronym = english_acronym;
	}

	public String getDutchName() {
		return dutch_name;
	}

	public String getDutchAcronym() {
		return dutch_acronym;
	}

	public String getEnglishName() {
		return name().toLowerCase().replace("_", "/");
	}

	public String getEnglishAcronym() {
		return english_acronym;
	}

	public boolean is(String pos) {
		if (System.getProperty("lang").equals("en"))
			return pos.startsWith(getEnglishAcronym());
		else
			return pos.startsWith(getDutchAcronym());
	}
}
