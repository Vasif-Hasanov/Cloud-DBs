import org.apache.log4j.Logger;

import server.models.Server;
import commons.utils.UtilMethods;



public class Application {
	private static final Logger LOGGER = Logger.getLogger(Application.class);
	/**
     * Main entry point for the echo server application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
    	try {
    		UtilMethods.initConsoleLogging();
    		LOGGER.info("Program begins.");
			if(args.length != 3) {
				System.out.println("Error! Invalid number of arguments!");
				System.out.println("Usage: Server <port>!");
			} else {
				int port = Integer.parseInt(args[0]);
				int cacheSize=Integer.parseInt(args[1]);
				String cacheType=args[2];
				
				Server server = new Server();
				server.setPort(port);
				server.setCacheSize(cacheSize);
				server.setCacheType(cacheType);
				server.start();
			}
		} catch (NumberFormatException nfe) {
			System.out.println("Error! Invalid argument <port>! Not a number!");
			System.out.println("Usage: Server <port>!");
			System.exit(1);
		}
    }
}
