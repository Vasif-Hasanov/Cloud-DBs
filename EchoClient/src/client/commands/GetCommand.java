package client.commands;

import java.io.PrintStream;

import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.ClientLogic;



public class GetCommand extends EchoCommand {
	private String key;
	public GetCommand(ClientLogic clientLogic, PrintStream userOut) {
		super(clientLogic, null, userOut);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException {
		// TODO Auto-generated method stub
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length != 2 || !"get".equals(tokens[0])){
			throw new UnknownCommandException();
		}
		
		key = tokens[1].trim();
	}

	@Override
	public boolean execute() throws Exception {
		clientLogic.asyncGet(key);
		return false;
	}

}
