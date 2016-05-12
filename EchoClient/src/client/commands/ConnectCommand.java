package client.commands;

import java.io.*;
import java.net.*;

import commons.exceptions.InvalidParameterException;
import commons.exceptions.InvalidParameterType;
import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.ClientLogic;
import client.models.Client;


public class ConnectCommand extends EchoCommand {
	private int port;
	private String host;
	public ConnectCommand(ClientLogic clientLogic, PrintStream userOut) {
		super(clientLogic, null, userOut);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException, InvalidParameterException {
		// TODO Auto-generated method stub
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length != 3 || !"connect".equals(tokens[0])){
			throw new UnknownCommandException();
		}
		
		try {
            port = Integer.parseInt(tokens[2]);
        } catch (NumberFormatException ex) {
        	throw new InvalidParameterException(
        			InvalidParameterException.getErrorMessage(
        					InvalidParameterType.PARAMETER_TYPE_INVALID, tokens[2]));
        }
		host = tokens[1];
	}

	@Override
	public boolean execute() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		try{
			clientLogic.connect(host, port, true);
			userOut.print(Client.PROMT);
		}catch (Exception e) {
			userOut.println("Connection failed!");
			userOut.print(Client.PROMT);
		}
		return false;
	}

}
