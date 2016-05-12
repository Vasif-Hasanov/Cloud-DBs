package client.commands;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import commons.exceptions.InvalidParameterException;
import commons.exceptions.InvalidParameterType;
import commons.exceptions.UnknownCommandException;
import commons.utils.UtilMethods;

import client.models.Client;


public class SetLogLevelCommand extends EchoCommand {
	private String logLevel;
	private List<String> allLevels = new ArrayList<String>(Arrays.asList(new String[]{"ALL","DEBUG","INFO","WARN","ERROR","FATAL","OFF"}));
	
	public SetLogLevelCommand(PrintStream userOut) {
		super(null, null, userOut);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void parseCommandText(String text) throws UnknownCommandException, InvalidParameterException {
		// TODO Auto-generated method stub
		String[] tokens = UtilMethods.getTokens(text);
		if (tokens.length != 2 || !"logLevel".equals(tokens[0])){
			throw new UnknownCommandException();
		}
		if (!allLevels.contains(tokens[1])){
			throw new InvalidParameterException(
					InvalidParameterException.getErrorMessage(
							InvalidParameterType.PARAMETER_TYPE_INVALID, tokens[1]));
		}
		logLevel = tokens[1];
	}

	@Override
	public boolean execute() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		LogManager.getRootLogger().setLevel(Level.toLevel(logLevel));
		userOut.print(Client.PROMT);
		userOut.println(logLevel);
		userOut.print(Client.PROMT);
		return false;
	}

}
