package testing;

import java.io.File;

import commons.utils.UtilMethods;
import config.models.ConfigServer;

import junit.framework.Test;
import junit.framework.TestSuite;


public class AllTests {
	static ConfigServer server;
	static {
		UtilMethods.initLogging();
		
		String localDir = System.getProperty("user.dir");
        String configFile = localDir + File.separator + "config" + File.separator + "servers.txt";
		
		try {
			server = new ConfigServer(configFile);
			server.start();	
			server.runCommand("start");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Scalable Storage ServerTest-Suite");
		clientSuite.addTestSuite(ConnectionTest.class);
		clientSuite.addTestSuite(InteractionTest.class); 
		clientSuite.addTestSuite(AdditionalTest.class); 
		return clientSuite;
	}
	
}
