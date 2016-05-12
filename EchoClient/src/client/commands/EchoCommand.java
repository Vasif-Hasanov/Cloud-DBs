package client.commands;

import java.io.*;
import org.apache.log4j.Logger;

import commons.exceptions.InvalidParameterException;
import commons.exceptions.UnknownCommandException;

import client.models.ClientLogic;

public abstract class EchoCommand {
	protected BufferedReader userIn = null;
	protected PrintStream userOut = null;
	protected ClientLogic clientLogic = null;
	protected final Logger LOGGER = Logger.getLogger(this.getClass());
	
	public EchoCommand(ClientLogic clientLogic, BufferedReader userIn, PrintStream userOut){
		this.userIn = userIn;
		this.userOut = userOut;
		this.clientLogic = clientLogic;
	}
	public abstract void parseCommandText(String text) throws UnknownCommandException, InvalidParameterException;
	public abstract boolean execute() throws Exception;
}
