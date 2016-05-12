package client.models;

import java.util.ArrayList;

import commons.metaData.ServerInfo;

public interface MetaInfoHolder {
	ServerInfo getConnectedServer();
	void setConnectedServer(ServerInfo serverInfo);
	ArrayList<ServerInfo> getRunningServerNodes();
	void setRunningServerNodes(ArrayList<ServerInfo> runningServerNodes);
}
