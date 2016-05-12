package client.commands;

import java.io.*;
import java.net.*;

import commons.exceptions.InvalidParameterException;
import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.ClientLogic;

public class SendCommand extends EchoCommand {
	private String messageBody;
	
	public SendCommand(ClientLogic clientLogic, PrintStream userOut) {
		super(clientLogic, null, userOut);
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException {
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length < 2 || !"send".equals(tokens[0])){
			throw new UnknownCommandException();
		}
		
		messageBody = text.substring(4).trim();
	}

	@Override
	public boolean execute() throws UnknownHostException, IOException, InvalidParameterException {
		clientLogic.asyncSend(messageBody);
		return false;
	}
}
