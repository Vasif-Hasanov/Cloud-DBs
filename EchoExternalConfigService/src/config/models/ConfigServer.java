package config.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import commons.communication.ServerDialogue;
import commons.metaData.ServerInfo;

public class ConfigServer extends Thread {
	public static final String PROMT = "EchoConfigServer> ";
	private Logger LOGGER = Logger.getLogger(ConfigServer.class);
	private BufferedReader userIn;
	private PrintStream userOut;
	private ServerSocket serverSocket;
    public boolean running = true;
	
    private ConfigServerLogic logic = new ConfigServerLogic(this);
    public ArrayList<ServerInfo> serverNodes = new ArrayList<ServerInfo>();
	public ArrayList<ServerInfo> runningServerNodes = new ArrayList<ServerInfo>();
	public ServerInfo configServerInfo;
	private String configPath = "/config/servers.txt";
	private static final String helpText = "Under construction...";
    
    public ConfigServer(String configPath) throws Exception {
    	this.configPath = configPath;
		logic.initServers(configPath);
		userIn = new BufferedReader(new InputStreamReader(System.in));
		userOut = System.out;
	}

    public void serveClient() {
		userOut.print(PROMT);
		while (true) {
			try {
				String next = userIn.readLine();
				runCommand(next);
				userOut.print(PROMT);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
				userOut.println(ex.getMessage());
				userOut.print(PROMT);
			}
		}
	}

	public void runCommand(String next) throws Exception {
		if ("init".equals(next)){
			logic.initServers(configPath);
		}
		if ("start".equals(next)){
			logic.startAll();
		}
		if ("stop".equals(next)){
			logic.stopAll();
		}
		if ("add".equals(next)){
			AddingOrRemovingResult addingResult = logic.addServerNodeBegin();
			if (addingResult.serverName == null){
				userOut.println("No server to add.");
			}else{
				userOut.println("Adding new server(" + addingResult.serverName + ") began...");
				if (addingResult.completed){
					logic.updateMetaInfos(addingResult.serverName);
					userOut.println("Adding new server(" + addingResult.serverName + ") ended...");
				}
			}	
		}
		if ("remove".equals(next)){
			String serverName = logic.removeServerNodeBegin();
			if (serverName == null){
				userOut.println("No server to remove.");
			}else{
				userOut.println("Removing new server(" + serverName + ") began...");
			}
		}
		if ("shutdown".equals(next)){
			logic.shutdownAll();
		}
		if ("help".equals(next)){
			userOut.println(helpText );
		}
		if ("listAll".equals(next)){
			listAllServerInfos();
		}
		if ("listRunning".equals(next)){
			listRunningServerInfos();
		}
	}
    
    private void listRunningServerInfos() {
    	for(ServerInfo item: runningServerNodes){
			userOut.println(item.toString());
		}
	}

	public void run() {     
    	running = initializeServerSocket();
        
        if(serverSocket != null) {
	        while(running){
	            try {
	                Socket client = serverSocket.accept();                
	                ServerDialogue connection = 
	                		new ServerDialogue(client);
	                ConfigSocketListener listener = new ConfigSocketListener(logic, userOut);
	                connection.addListener(listener);
	                new Thread(connection).start();
	                
	                LOGGER.info("Connected to " 
	                		+ client.getInetAddress().getHostName() 
	                		+  " on port " + client.getPort());
	            } catch (IOException e) {
	            	LOGGER.error("Error! " +
	            			"Unable to establish connection. \n", e);
	            }
	        }
        }
        LOGGER.info("Server stopped.");
    }	
    
	private void listAllServerInfos() {
		for(ServerInfo item: serverNodes){
			userOut.println(item.toString());
		}
	}

	private boolean initializeServerSocket() {
    	LOGGER.info("Initialize config server ...");
    	try {
            serverSocket = new ServerSocket(configServerInfo.getPort());
            LOGGER.info("Server listening on port: " 
            		+ serverSocket.getLocalPort());    
            return true;
        } catch (IOException e) {
        	LOGGER.error("Error! Cannot open server socket:");
            if(e instanceof BindException){
            	LOGGER.error("Port " + configServerInfo.getPort() + " is already bound!");
            }
            return false;
        }
    }    
}
