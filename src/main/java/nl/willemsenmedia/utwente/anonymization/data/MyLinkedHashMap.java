package nl.willemsenmedia.utwente.anonymization.data;

import org.apache.commons.lang3.StringUtils;

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
			Set<K> keys_in_keyList = new HashSet<>(keyList);
			keys_in_keyList.removeAll(this.keySet());
			Set<K> keys_in_keyset = new HashSet<>(keySet());
			keys_in_keyList.removeAll(this.keyList);
			throw new RuntimeException("De lengte van de keylist en de hashmap is niet meer gelijk. Dat is raar! Het verschil (wel in keylist maar niet in keyset): " + StringUtils.join(keys_in_keyList, ", ") + ". (wel in keyset maar niet in keyList): " + StringUtils.join(keys_in_keyset, ", ") + ".");
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

	public Map.Entry<K, V> getEntryFromIndex(int index) {
		K key = keyList.get(index);
		return new DefaultEntry<>(key, get(key));
	}
}
