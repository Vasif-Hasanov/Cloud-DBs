import java.io.File;

import org.apache.log4j.Logger;

import commons.utils.UtilMethods;
import config.models.ConfigServer;



public class Program {
	private static final Logger LOGGER = Logger.getLogger(Program.class);
	/**
     * Main entry point for the config server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
    	try {
    		UtilMethods.initLogging();
    		LOGGER.info("Application begins.");
			
			String localDir = System.getProperty("user.dir");
	        String configFile = localDir + File.separator + "config" + File.separator + "servers.txt";
			ConfigServer server = new ConfigServer(configFile);
			server.start();			
			server.serveClient();
		} catch (Exception e) {
			System.out.println("Error!");
			LOGGER.error(e);
			System.exit(1);
		}
    }
}
