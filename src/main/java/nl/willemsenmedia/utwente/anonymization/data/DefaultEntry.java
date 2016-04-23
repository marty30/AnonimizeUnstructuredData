package nl.willemsenmedia.utwente.anonymization.data;

import java.util.Map;

/**
 * Created by Martijn on 23-4-2016.
 */
public class DefaultEntry<K, V> implements Map.Entry<K, V> {
	private K key;
	private V value;

	public DefaultEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		return this.value = value;
	}
}
