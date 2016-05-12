package config.models;

import java.io.IOException;

import commons.exceptions.AnException;
import commons.metaData.ServerInfo;

public class ConfigServerToComputerLogic {
	Process proc;
	Runtime run = Runtime.getRuntime();
	String runScriptForWindowsThroughSSH = "ssh -n %s java -jar D://server.jar %d %d %s";
	String runScriptForWindows = "cmd /k start cmd /k cmd /k java -jar D://serverm3.jar ";
	public void runJarWithSSH(ServerInfo node) throws AnException, IOException{
		String commandSSH = String.format(runScriptForWindowsThroughSSH, node.getServerIp(), node.getPort(),node.getCacheSize(),node.getCacheType()); 
		//String command = runScriptForWindows + node.getPort();
		String command = runScriptForWindows +" "+ node.getPort()+" "+node.getCacheSize()+" "+node.getCacheType();
		proc = run.exec(command);
	}	
}
