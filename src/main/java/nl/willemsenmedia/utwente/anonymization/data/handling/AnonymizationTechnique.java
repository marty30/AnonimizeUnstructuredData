package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataModifier;
import nl.willemsenmedia.utwente.anonymization.nlp.NLPHelper;
import nl.willemsenmedia.utwente.anonymization.nlp.OpenNLPFactory;
import nl.willemsenmedia.utwente.anonymization.nlp.POS;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import javax.xml.bind.JAXBElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * The abstract class that all anonymization techniques should extend.
 */
public abstract class AnonymizationTechnique {

	protected boolean doStem = true;

	public static String find(String text, String regex, Function<Map, String> manipulation) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("matcher", matcher);
		parameters.put("text", text);
		parameters.put("regex", regex);
		// Check all occurrences
		while (matcher.find()) {
			text = manipulation.apply(parameters);
		}
		return text;
	}

	/**
	 * This method really anonimizes the data. It is given a data-entry and a settings class to determine how to attack certain problems that are specified by the user.
	 *
	 * @param dataEntry the data
	 * @param raw_data the raw data
	 * @param settings  the settings
	 * @return a NEW data object
	 */
	//@requires dataEntry != null && dataEntry.getDataAttributes() != null && dataEntry.getDataAttributes().size() > 0
	//@ensures return != null && return.getDataAttributes() != null && return.getDataAttributes().size() > 0
	public abstract DataEntry anonymize(DataEntry dataEntry, List<DataEntry> raw_data, Settings settings);

	/**
	 * <setting name="verwijder_lidwoorden" type="java.lang.Boolean" value="true" overwritable="false"/>
	 * <setting name="verwijder_voorzetsels" type="java.lang.Boolean" value="true" overwritable="false"/>
	 * <setting name="maak_alle_woorden_lowercase" type="java.lang.Boolean" value="true" overwritable="true"/>
	 * <setting name="maak_beginwoorden_lowercase" type="java.lang.Boolean" value="true" overwritable="true"/>
	 * <setting name="verwijder_leestekens" type="java.lang.Boolean" value="true" overwritable="false"/>
	 * <setting name="verwijder_datums" type="java.lang.Boolean" value="false" overwritable="true"/>
	 * <setting name="regexes" screenname="RegEx-en die uitgevoerd moeten worden als deel van het pre-processing"
	 * type="java.util.Map" overwritable="true">
	 * <entry regex_search="\b([Pp]|[Vv])\b" regex_replace="vader" comment="P -> vader"/>
	 * <entry regex_search="\b([Mm]|[dr])\b" regex_replace="moeder" comment="M -> moeder"/>
	 * </setting>
	 *
	 * @param dataEntry
	 * @param settings
	 * @return
	 */
	public DataEntry doPreProcessing(DataEntry dataEntry, Settings settings) {
		for (DataAttribute attr : dataEntry.getDataAttributes()) {
			attr.setData(attr.getData().replaceAll("\\r\\n", "\r\n"));
			//Remove punctuation
			if (settings.getSettingsMap().get("verwijder_leestekens").getValue().equals("true")) {
				attr.setData(attr.getData().replaceAll("[^\\w\\s\\.]", ""));
			}
			// Handle specific regexes
			settings.getSettingsMap().get("regexes").getContent().stream().filter(regex_list_entry -> regex_list_entry instanceof JAXBElement).forEach(regex_list_entry -> {
				Settings.Setting.Entry entry = (Settings.Setting.Entry) ((JAXBElement) regex_list_entry).getValue();
				attr.setData(attr.getData().replaceAll(entry.getRegexSearch(), entry.getRegexReplace()));
			});
			//Now remove stopwords
			if (settings.getSettingsMap().get("verwijder_lidwoorden").getValue().equals("true")) {
				Settings.Setting.Entry entry = new Settings.Setting.Entry();
				entry.setRegexReplace(" ");
				if ("en".equals(System.getProperty("lang"))) {
					entry.setRegexSearch("( [Tt]he | [Aa] | [Aa]n )");
				} else {
					entry.setRegexSearch("( [Dd]e | [Hh]et | [Ee]en )");
				}
				attr.setData(attr.getData().replaceAll(entry.getRegexSearch(), entry.getRegexReplace()));
			}

			//Handle the case of the words
			if (settings.getSettingsMap().get("maak_alle_woorden_lowercase").getValue().equals("true")) {
				attr.setData(attr.getData().toLowerCase());
			} else if (settings.getSettingsMap().get("maak_beginwoorden_lowercase").getValue().equals("true")) {
				attr.setData(find(attr.getData(), "^([A-Z]{1})([^A-Z])|(\\. |\\n)([A-Z]{1})([^A-Z])", parameters -> {
					Matcher matcher = (Matcher) parameters.get("matcher");
					String text = (String) parameters.get("text");
					return text.replace(matcher.group(), matcher.group().toLowerCase());
				}));
			}
			//Now remove any prepositios and dates
			String[] tokens = OpenNLPFactory.getTokenizer().tokenize(attr.getData());
			if (settings.getSettingsMap().get("verwijder_voorzetsels").getValue().equals("true") || settings.getSettingsMap().get("verwijder_datums").getValue().equals("true")) {
				String[] tagged_tokens = OpenNLPFactory.getPOSTagger().tag(tokens);
				for (int i = 0; i < tagged_tokens.length; i++) {
					if (POS.PREPOSITION.is(tagged_tokens[i]) || (settings.getSettingsMap().get("verwijder_datums").getValue().equals("true") && NLPHelper.isDate(tokens[i]))) {
						attr.setData(attr.getData().replace(tokens[i] + " ", ""));
					}

				}
			}
			//Finally stem the whole thing
			if (doStem) {
				for (String token : tokens) {
					attr.setData(attr.getData().replace(token, DataModifier.getStem(token)));
				}
			}
		}
		return dataEntry;
	}
}
