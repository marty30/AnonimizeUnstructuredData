package nl.willemsenmedia.utwente.anonymization.data;

import java.util.*;

/**
 * Created by Martijn on 23-4-2016.
 */
public class MyLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

	private List<K> keyList = new LinkedList<>();

	@Override
	public V put(K key, V value) {
		V old_value;
		if (keyList.contains(key)) {
			old_value = super.put(key, value);
			assert old_value != null;
		} else {
			keyList.add(key);
			old_value = super.put(key, value);
			assert old_value == null;
		}
		if (keyList.size() != this.size()) {
			throw new RuntimeException("De lengte van de keylist en de hashmap is niet meer gelijk. Dat is raar!");
		} else if (!keySet().equals(new HashSet<>(keyList))) {
			throw new RuntimeException("De keylist en de keyset zijn niet meer gelijk. Dat is raar!");
		}
		return old_value;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public int indexOf(K key) {
		return keyList.indexOf(key);
	}

	public boolean contains(K key) {
		return keyList.contains(key);
	}
}
