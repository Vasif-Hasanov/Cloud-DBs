package client.models;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import client.commands.*;
import commons.exceptions.*;
import commons.metaData.ServerInfo;

public class Client implements MetaInfoHolder{
	private BufferedReader userIn;
	private PrintStream userOut;
	public static final String PROMT = "EchoClient> ";
	public HashMap<String, EchoCommand> allCommands = new HashMap<String, EchoCommand>();
	
	private ClientLogic clientLogic;
	private ClientSocketListener listener;
	private ServerInfo connectedServer;
	private ArrayList<ServerInfo> runningServerNodes = null;
	private static final Logger LOGGER = Logger.getLogger(Client.class);
	
	public Client() {
		userIn = new BufferedReader(new InputStreamReader(System.in));
		userOut = System.out;
		clientLogic = new ClientLogic(this);
		listener = new ClientSocketListener(userOut, clientLogic);
		clientLogic.addListener(listener);
		allCommands.put("con", new ConnectCommand(clientLogic, userOut));
		allCommands.put("dis", new DisconnectCommand(clientLogic, userOut));
		allCommands.put("hel", new HelpCommand(userOut));
		allCommands.put("qui", new QuitCommand(clientLogic, userOut));
		allCommands.put("sen", new SendCommand(clientLogic, userOut));
		allCommands.put("log", new SetLogLevelCommand(userOut));
		allCommands.put("get", new GetCommand(clientLogic, userOut));
		allCommands.put("put", new PutCommand(clientLogic, userOut));
	}

	public void doJob() {
		userOut.print(PROMT);
		while (true) {
			try {
				boolean isExiting = runNextCommand();
				if (isExiting) {
					break;
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				userOut.print(PROMT);
				userOut.println(ex.getMessage());
				userOut.print(PROMT);
			}
		}
	}

	private boolean runNextCommand() throws Exception {
		String nextCommandText = userIn.readLine();
		if (nextCommandText == null
				|| nextCommandText.trim().length() < 4) {
			throw new UnknownCommandException();
		}
		nextCommandText = nextCommandText.trim();
		String nextCommandTextBeginning = nextCommandText.substring(0,
				3);
		EchoCommand nextCommand = allCommands
				.get(nextCommandTextBeginning);
		nextCommand.parseCommandText(nextCommandText);
		boolean isExiting = nextCommand.execute();
		return isExiting;
	}

	@Override
	public ServerInfo getConnectedServer() {
		// TODO Auto-generated method stub
		return connectedServer;
	}

	@Override
	public ArrayList<ServerInfo> getRunningServerNodes() {
		// TODO Auto-generated method stub
		return runningServerNodes;
	}

	@Override
	public void setConnectedServer(ServerInfo serverInfo) {
		connectedServer = serverInfo;
	}

	@Override
	public void setRunningServerNodes(ArrayList<ServerInfo> runningServerNodes) {
		this.runningServerNodes = runningServerNodes;
	}

	
}
