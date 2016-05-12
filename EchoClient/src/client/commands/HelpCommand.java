package client.commands;

import java.io.*;
import java.net.*;

import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.Client;


public class HelpCommand extends EchoCommand {
	public HelpCommand(PrintStream userOut) {
		super(null, null, userOut);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException {
		// TODO Auto-generated method stub
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length != 1 || !"help".equals(tokens[0])){
			throw new UnknownCommandException();
		}
	}

	@Override
	public boolean execute() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		userOut.print(Client.PROMT);
		userOut.println("Help is under constraction...");
		userOut.print(Client.PROMT);
		return false;
	}

}
