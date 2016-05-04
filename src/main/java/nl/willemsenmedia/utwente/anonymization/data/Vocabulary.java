package nl.willemsenmedia.utwente.anonymization.data;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by Martijn on 21-4-2016.
 */
public class Vocabulary implements Cloneable {
	MyLinkedHashMap<String, Integer> voc = new MyLinkedHashMap<>();
	LinkedList<String> classes = new LinkedList<>();

	public static Map<Integer, Integer> createCleanWordcountMap(Vocabulary voc, DataEntry dataEntry) {
		Map<Integer, Integer> map = new HashMap<>();
		dataEntry.getDataAttributes().stream().filter(dataAttribute -> !dataAttribute.getDataType().equals(DataType.CLASS)).forEach(dataAttribute -> {
			StringTokenizer tokenizer = new StringTokenizer(dataAttribute.getData(), " \t\n\r\f,\\.;");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken().toLowerCase();
				int key = voc.getIndex(token);
				if (map.containsKey(key)) {
					map.put(key, map.get(key) + 1);
				} else {
					map.put(key, 1);
				}
			}
		});
		return map;
	}

	public void addData(DataEntry dataEntry) {
		dataEntry.getDataAttributes().forEach(this::addData);
	}

	public void addData(DataAttribute dataAttribute) {
		if (!dataAttribute.getDataType().equals(DataType.CLASS)) {
			StringTokenizer tokenizer = new StringTokenizer(dataAttribute.getData(), " \t\n\r\f,\\.;");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken().toLowerCase();
				if (voc.contains(token))
					voc.put(token, voc.get(token) + 1);
				else
					voc.put(token, 1);
			}
		} else {
			if (!classes.contains(dataAttribute.getData())) classes.add(dataAttribute.getData());
		}
	}

	public int getIndex(String token) {
		return voc.indexOf(token);
	}

	public int getClassIndex(String data) {
		return classes.indexOf(data);
	}

	public Map<String, Integer> getWordcountMap() {
		return voc;
	}

	public String getWord(int key) {
		return voc.getEntryFromIndex(key).getKey();
	}

	public void removeData(DataEntry dataEntry, String... tokensToIgnore) {
		dataEntry.getDataAttributes().forEach(dataAttribute -> removeData(dataAttribute, tokensToIgnore));
	}

	public void removeData(DataAttribute dataAttribute, String... tokensToIgnore) {
		List<String> tokensToIgnoreList = Arrays.asList(tokensToIgnore);
		if (!dataAttribute.getDataType().equals(DataType.CLASS)) {
			StringTokenizer tokenizer = new StringTokenizer(dataAttribute.getData(), " \t\n\r\f,\\.;");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken().toLowerCase();
				if (!tokensToIgnoreList.contains(token)) {
					if (voc.contains(token)) {
						if (voc.get(token).equals(0))
							System.err.println("Kon " + token + " niet verwijderen uit de vocabulary want er waren maar 0 entries. Het lijkt allemaal niet helemaal goed toegevoegd te zijn...");
						else
							voc.put(token, voc.get(token) - 1);
					} else {
						System.err.println("Kon " + token + " niet verwijderen uit de vocabulary want het word bestond niet in de voc...");
					}
				}
			}
		} else {
			if (!classes.contains(dataAttribute.getData())) classes.add(dataAttribute.getData());
		}
	}

	public void changeWord(String old_word, String new_word, DataEntry dataEntry, String... tokensToIgnore) {
		List<String> tokensToIgnoreList = Arrays.asList(tokensToIgnore);
		if (!tokensToIgnoreList.contains(old_word)) {
			//Find the number of occurrences
			int occurrences = 0;
			for (DataAttribute dataAttribute : dataEntry.getDataAttributes()) {
				occurrences += StringUtils.countMatches(dataAttribute.getData(), " " + old_word + " ");
			}
			// Now change it in the voc if the number of occurrences is larger than 0
			if (occurrences > 0) {
				if (voc.contains(old_word)) {
					if (voc.get(old_word) < occurrences)
						System.err.println("Kon " + old_word + " niet verwijderen uit de vocabulary want er waren maar 0 entries. Het lijkt allemaal niet helemaal goed toegevoegd te zijn...");
					else
						voc.put(old_word, voc.get(old_word) - occurrences);
				} else {
					System.err.println("Kon " + old_word + " niet verwijderen uit de vocabulary want het word bestond niet in de voc...");
				}
				if (voc.contains(new_word))
					voc.put(new_word, voc.get(new_word) + occurrences);
				else
					voc.put(new_word, occurrences);
			}
		}
	}

	@SuppressWarnings("CloneDoesntCallSuperClone")
	@Override
	public Vocabulary clone() {
		Vocabulary new_voc = new Vocabulary();
		new_voc.voc = voc.clone();
		new_voc.classes.addAll(classes);
		return new_voc;
	}
}
