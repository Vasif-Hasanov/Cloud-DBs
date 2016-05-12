import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

import commons.utils.UtilMethods;

import client.models.Client;



public class Program {
	private static final Logger LOGGER = Logger.getLogger(Program.class);
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		UtilMethods.initLogging();
		LOGGER.info("App begins...");
		Client consoleApp = new Client();
		consoleApp.doJob();
		LOGGER.info("App ends...");
	}

	

}
