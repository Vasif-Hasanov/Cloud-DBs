package commons.metaData;

import java.io.Serializable;

import commons.utils.UtilMethods;

public class ServerInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String min = "00000000000000000000000000000000";
	public static String max = "ffffffffffffffffffffffffffffffff";

	private int port;
	private String serverName;
	private String serverIp;
	
	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	public int getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	private String hashFrom;
	private String hashTo;
		
	private String cacheType;
	private int cacheSize;

	private ServerNodeStatus status;
    
	public String getHash() {
		return UtilMethods.getHashMD5(getDesc());
	}
	
	public String getDesc(){
		String desc = serverIp + ":" + port;
		return desc;
	}
	
	 
	
	public ServerInfo(String serverName, String serverIp, int port){
		this.serverName = serverName;
		this.serverIp = serverIp;
		this.port = port;
		this.status = ServerNodeStatus.IDLE;
	}
	
	public ServerInfo(String serverName, String serverIp, int port,
			int cacheSize, String cacheType) {
		this.serverName = serverName;
		this.serverIp = serverIp;
		this.port = port;
		this.cacheType = cacheType;
		this.cacheSize = cacheSize;
		this.status = ServerNodeStatus.IDLE;
	}

	public String getServerName() {
		return serverName;
	}

	public int getPort() {
		return port;
	}

	public ServerNodeStatus getStatus() {
		return status;
	}
	public void setStatus(ServerNodeStatus status) {
		this.status = status;
	}

	public String getHashFrom() {
		return hashFrom;
	}

	public void setHashFrom(String hashFrom) {
		this.hashFrom = hashFrom;
	}

	public String getHashTo() {
		return hashTo;
	}

	public void setHashTo(String hashTo) {
		this.hashTo = hashTo;
	}
	
	public boolean isInsideKeyRange(String key){
		String hashOfKey = UtilMethods.getHashMD5(key);
		boolean isZeroPointCrossing = (hashFrom.compareTo(hashTo) >= 0);
		boolean result;
		if (isZeroPointCrossing){
			result = (hashFrom.compareTo(hashOfKey) < 0 && hashOfKey.compareTo(max) < 0)
					|| (min.compareTo(hashOfKey) < 0 && hashOfKey.compareTo(hashTo) < 0);
		}else{
			result = hashFrom.compareTo(hashOfKey) < 0 && hashOfKey.compareTo(hashTo) < 0;
		}
		return result;
	}

	public String getServerIp() {
		return serverIp;
	}

	@Override
	public String toString() {
		return serverName + " " + status.name() + " " + hashFrom + " - " + hashTo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ServerInfo)
            return getServerName().equals(((ServerInfo)obj).getServerName());
        else
            return false;
	}
}
