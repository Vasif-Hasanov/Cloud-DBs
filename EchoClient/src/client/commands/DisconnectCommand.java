package client.commands;

import java.io.*;

import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.ClientLogic;
import client.models.Client;

public class DisconnectCommand extends EchoCommand {
	public DisconnectCommand(ClientLogic clientLogic, PrintStream userOut) {
		super(clientLogic, null, userOut);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException {
		// TODO Auto-generated method stub
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length != 1 || !"disconnect".equals(tokens[0])){
			throw new UnknownCommandException();
		}
	}

	@Override
	public boolean execute(){
		// TODO Auto-generated method stub
		clientLogic.disconnect();
		userOut.print(Client.PROMT);
		userOut.println("Connection terminated.");
		userOut.print(Client.PROMT);
		return false;
	}

}
