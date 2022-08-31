package com.allendowney.thinkdast;

import java.util.List;
import java.util.Map;

/**
 * Implementation of a HashMap using a collection of MyLinearMap and
 * resizing when there are too many entries.
 *
 * @author downey
 * @param <K>
 * @param <V>
 *
 */
public class MyHashMap<K, V> extends MyBetterMap<K, V> implements Map<K, V> {

  // Average number of entries per map before we rehash
  protected static final double FACTOR = 1.0;

  @Override
  public V put(K key, V value) {
    V oldValue = super.put(key, value);

    // Check if the number of elements per map exceeds the threshold
    if (size() > maps.size() * FACTOR) {
      rehash();
    }
    return oldValue;
  }

  /**
   * Doubles the number of maps and rehashes the existing entries.
   */
  protected void rehash() {
    // Save the existing entries
		List<MyLinearMap<K, V>> oldMaps = maps;

		// Make more maps
		int k = maps.size() * 2;
		makeMaps(k);

		// Put the entries into the new map
		for (MyLinearMap<K, V> map: oldMaps) {
			for (Map.Entry<K, V> entry: map.getEntries()) {
				put(entry.getKey(), entry.getValue());
			}
		}
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    Map<String, Integer> map = new MyHashMap<String, Integer>();
    for (int i = 0; i < 10; i++) {
      map.put(new Integer(i).toString(), i);
    }
    Integer value = map.get("3");
    System.out.println(value);
  }
}
