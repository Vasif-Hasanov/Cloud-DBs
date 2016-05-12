package server.caching;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

public class LruFifoCache extends LinkedHashMap<String, String> {
	private Logger LOGGER = Logger.getLogger(LruFifoCache.class);
	int maxCapacity;

	public LruFifoCache(int maxCapacity, float loadFactor, boolean cacheType) {
		super(maxCapacity, loadFactor, cacheType);
		this.maxCapacity = maxCapacity;

	}

	protected boolean removeEldestEntry() {
		return size() >= this.maxCapacity;
	}
}
