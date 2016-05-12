package client.commands;

import java.io.*;
import java.net.*;

import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.ClientLogic;


public class QuitCommand extends EchoCommand {
	public QuitCommand(ClientLogic clientLogic, PrintStream userOut) {
		super(clientLogic, null, userOut);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException {
		// TODO Auto-generated method stub
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length != 1 || !"quit".equals(tokens[0])){
			throw new UnknownCommandException();
		}
	}

	@Override
	public boolean execute() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		clientLogic.disconnect();
		userOut.println("Application exit!");
		return true;
	}

}
