package server.models;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;

import server.caching.FilePersistence;
import server.caching.LFU;
import server.caching.LruFifoCache;
import commons.communication.ServerDialogue;
import commons.data.KVMessage;
import commons.data.MessageStatusType;
import commons.metaData.ServerInfo;

public class ServerForClientLogic {
	
	private static LinkedHashMap pairs;
	private ServerDialogue dialogue;
	private Server server;
	private static FilePersistence file = new FilePersistence();
	private String lru = "LRU";
	private String lfu = "LFU";
	private String fifo = "FIFO";
	public ServerForClientLogic(ServerDialogue dialogue, Server server) {
		this.dialogue = dialogue;
		this.server = server;
	}
	
	public ServerForClientLogic(ServerDialogue dialogue, Server server,
			int cacheSize, String cacheType) {
		this.dialogue = dialogue;
		this.server = server;
		if (cacheType.equalsIgnoreCase(fifo)) {

			pairs = new LruFifoCache(cacheSize, 0.75f, false);

		} else if (cacheType.equalsIgnoreCase(lru)) {

			pairs = new LruFifoCache(cacheSize, 0.75f, true);
		} else if (cacheType.equalsIgnoreCase(lfu)) {
			pairs = new LFU(cacheSize, 0.75f);
		}
	}
	public String get(KVMessage msg) throws IOException {
		String value = null;
		synchronized (pairs) {
			value = (String) pairs.get(msg.key);
		}
		KVMessage response = null;
		if (contains(msg.key)) {
			
			response = new KVMessage(msg.key, value,
					MessageStatusType.GET_SUCCESS);
		} else {
			response = new KVMessage(msg.key, null, MessageStatusType.GET_ERROR);
		}
		dialogue.asyncSend(response);
		return value;
	}

	private boolean contains(String key) {
		synchronized (pairs) {
			return pairs.containsKey(key);
		}
	}

	public void put(KVMessage msg) throws IOException {
		if (server.lockedForWrite) {
			handleRequestsInWriteLockMode(msg);
			return;
		}
		KVMessage response = msg;
		if ("null".equals(msg.value)) {
			msg.status = MessageStatusType.DELETE_SUCCESS;
			delete(msg.key);
		} else {
			if (contains(msg.key)) {
				msg.status = MessageStatusType.PUT_UPDATE;
			} else {
				msg.status = MessageStatusType.PUT_SUCCESS;
			}
			synchronized (pairs) {
				pairs.put(msg.key, msg.value);
				file.write(msg.key, msg.value);
			}			
		}
		dialogue.asyncSend(response);
	}
	
	private void handleRequestsInWriteLockMode(KVMessage msg)
			throws IOException {
		msg.status = MessageStatusType.SERVER_AT_WRITE_LOCK;
		dialogue.asyncSend(msg);
	}
	
	private void delete(String key) {
		synchronized (pairs) {
			pairs.remove(key);
		}
	}

	public void deleteOldData(ServerInfo currentNode) {
		/*synchronized (pairs) {
			for (String key : pairs.keySet()) {
				if (!currentNode.isInsideKeyRange(key)) {
					pairs.remove(key);
				}
			}
		}*/
		synchronized (pairs) {
			Set<Entry<String, String>> mapValues = pairs.entrySet();
			Iterator iter = mapValues.iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = (Entry<String, String>)iter.next();
				if (!currentNode.isInsideKeyRange(entry.getKey())) {
					iter.remove();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String, String> getAllData(){
		Hashtable<String, String> clonedPairs = null;
		synchronized (pairs) {
			clonedPairs = (Hashtable<String, String>) pairs.clone();
		}
		return clonedPairs;
	}
}
