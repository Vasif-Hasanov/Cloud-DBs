package server.caching;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class LFU extends LinkedHashMap<String, KVPair> {
	private Logger LOGGER = Logger.getLogger(LFU.class);
	private static int initialCapacity = 10;

	public LFU(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		LFU.initialCapacity = initialCapacity;

	}

	public void put(String key, String value) {
		if (!isFull()) {
			KVPair kv = new KVPair(key, value);
			kv.setFrequency(0);
			super.put(key, kv);
		} else {
			String lfuKey = getLFUKey();
			super.remove(lfuKey);

			KVPair kv = new KVPair();
			kv.setKey(key);
			kv.setValue(value);
			kv.setFrequency(0);

			super.put(key, kv);
		}
	}

	public String getLFUKey() {
		String key = null;
		int minFreg = Integer.MAX_VALUE;
		for (Map.Entry<String, KVPair> entry : super.entrySet()) {
			if (minFreg > entry.getValue().frequency) {
				key = entry.getKey();
				minFreg = entry.getValue().frequency;
			}
		}

		return key;
	}

	public String get(String key) {
		if (super.containsKey(key)) // cache hit
		{
			KVPair kv = super.get(key);
			kv.frequency++;
			super.put(key, kv);
			return kv.getValue();
		}
		return null; // cache miss
	}

	public boolean isFull() {
		if (size() == initialCapacity) {
			return true;
		}

		return false;
	}

}
