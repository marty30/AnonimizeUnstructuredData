package nl.willemsenmedia.utwente.anonymization.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Martijn on 21-4-2016.
 */
public class Vocabulary {
	MyLinkedHashMap<String, Integer> voc = new MyLinkedHashMap<>();
	LinkedList<String> classes = new LinkedList<>();

	public static Map<Integer, Integer> createCleanWordcountMap(Vocabulary voc, DataEntry dataEntry) {
		Map<Integer, Integer> map = new HashMap<>();
		dataEntry.getDataAttributes().stream().filter(dataAttribute -> !dataAttribute.getDataType().equals(DataType.CLASS)).forEach(dataAttribute -> {
			StringTokenizer tokenizer = new StringTokenizer(dataAttribute.getData());
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
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
			StringTokenizer tokenizer = new StringTokenizer(dataAttribute.getData(), " \t\n\r\f,;");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
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
}
