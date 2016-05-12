package config.models;

import java.io.PrintStream;

import commons.communication.SocketListener;
import commons.communication.SocketStatus;
import commons.data.CommandToServerMessage;
import commons.data.Message;
import commons.metaData.ServerInfo;

public class ConfigSocketListener implements SocketListener {
	private ConfigServerLogic configServerLogic;
	private PrintStream userOut;
	public ConfigSocketListener(ConfigServerLogic configServerLogic, PrintStream userOut) {
		this.configServerLogic = configServerLogic;
		this.userOut = userOut;
	}

	@Override
	public void handleNewMessage(Message msg) throws Exception {
		if (msg.isCommandToServerMessage()) {
			CommandToServerMessage responce = (CommandToServerMessage) msg;
			switch (responce.status) {
			case ADD_DATA_MOVED:
				ServerInfo nodeAdded = configServerLogic.addServerNodeEnd(responce.serverName);
				userOut.println("Adding new server(" + nodeAdded.getServerName() + ") ended...");
				break;
			case REMOVE_DATA_MOVED:
				configServerLogic.removeServerNodeEnd(responce.serverName);
				userOut.println("Removing new server(" + responce.serverName + ") ended...");
				break;
			default:
				break;
			}			
		}
	}

	@Override
	public void handleStatus(SocketStatus status) {}

}
