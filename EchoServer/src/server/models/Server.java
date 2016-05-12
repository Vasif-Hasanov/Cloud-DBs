package server.models;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.IOException;

import org.apache.log4j.Logger;

import commons.communication.ServerDialogue;
import commons.metaData.ServerInfo;

/**
 * Represents a simple Echo Server implementation.
 */
public class Server extends Thread {
	private Logger LOGGER = Logger.getLogger(Server.class);
	private int port;
	private int cacheSize;
	private String cacheType;
	
    private ServerSocket serverSocket;
    public boolean running = true;
    public boolean holding = true;
    //public boolean holding = false;
    public boolean lockedForWrite = false;
	public ArrayList<ServerInfo> runningServerNodes;
	public String serverName = "unknown";
	public ServerInfo configServerInfo;
    
    /**
     * Constructs a (Echo-) Server object which listens to connection attempts 
     * at the given port.
     * 
     * @param port a port number which the Server is listening to in order to 
     * 		establish a socket connection to a client. The port number should 
     * 		reside in the range of dynamic ports, i.e 49152 – 65535.
     */
    
    /**
     * Initializes and starts the server. 
     * Loops until the the server should be closed.
     */
	public Server(int port, int cacheSize, String cacheType) {
		this.port = port;
		this.cacheSize = cacheSize;
		this.cacheType = cacheType;
	}
	
	
	
	public int getCacheSize() {
		return cacheSize;
	}



	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}



	public String getCacheType() {
		return cacheType;
	}



	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}



	public Server() {
	}
	
    public void run() {
        
    	running = initializeServer();
        
        if(serverSocket != null) {
	        while(running){
	            try {
	                Socket clientSocket = serverSocket.accept();                
	                ServerDialogue connection = 
	                		new ServerDialogue(clientSocket);
	               // ServerSocketListener logic = new ServerSocketListener(this, connection);
	                ServerSocketListener logic = new ServerSocketListener(this,connection, cacheSize, cacheType);
	                connection.addListener(logic);
	                new Thread(connection).start();
	                
	                LOGGER.info("Connected to "+ clientSocket.getInetAddress().getHostName()+  " on port " + clientSocket.getPort());
	            } catch (IOException e) {
	            	LOGGER.error("Error! " + "Unable to establish connection. \n", e);
	            }
	        }
        }
       // LOGGER.info("Server stopped.");
    }

    /**
     * Stops the server insofar that it won't listen at the given port any more.
     */
    public void stopServer(){
    	holding = true;
        running = false;
        try {
			serverSocket.close();
		} catch (IOException e) {
			LOGGER.error("Error! " +
					"Unable to close socket on port: " + port, e);
		}
    }

    private boolean initializeServer() {
    	LOGGER.info("Initialize server ...");
    	try {
            serverSocket = new ServerSocket(port);
            LOGGER.info("Server listening on port: " 
            		+ serverSocket.getLocalPort());    
            return true;
        
        } catch (IOException e) {
        	LOGGER.error("Error! Cannot open server socket:");
            if(e instanceof BindException){
            	LOGGER.error("Port " + port + " is already bound!");
            }
            return false;
        }
    }

	public void setPort(int port) {
		this.port = port;
	}
    
    
}
