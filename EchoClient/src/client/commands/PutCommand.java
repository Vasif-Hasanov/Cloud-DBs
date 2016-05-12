package client.commands;

import java.io.PrintStream;

import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.ClientLogic;

public class PutCommand extends EchoCommand {
	private String key;
	private String value;
	public PutCommand(ClientLogic clientLogic, PrintStream userOut) {
		super(clientLogic, null, userOut);
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException {
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length != 3 || !"put".equals(tokens[0])){
			throw new UnknownCommandException();
		}
		
		key = tokens[1].trim();
		if (tokens.length == 3){
			value = tokens[2].trim();
		}
	}

	@Override
	public boolean execute() throws Exception {
		clientLogic.asyncPut(key, value);
		return false;
	}

}
