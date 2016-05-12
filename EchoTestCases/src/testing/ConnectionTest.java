package testing;

import java.net.UnknownHostException;
import java.util.ArrayList;

import commons.metaData.ServerInfo;

import client.models.ClientLogic;
import client.models.MetaInfoHolder;
import junit.framework.TestCase;


public class ConnectionTest extends TestCase implements MetaInfoHolder{
	private ServerInfo connectedServer = null;
	private ArrayList<ServerInfo> runningServerNodes = null;

	@Override
	public ServerInfo getConnectedServer() {
		// TODO Auto-generated method stub
		return connectedServer;
	}

	@Override
	public ArrayList<ServerInfo> getRunningServerNodes() {
		// TODO Auto-generated method stub
		return runningServerNodes;
	}

	@Override
	public void setConnectedServer(ServerInfo serverInfo) {
		connectedServer = serverInfo;
	}

	@Override
	public void setRunningServerNodes(ArrayList<ServerInfo> runningServerNodes) {
		this.runningServerNodes = runningServerNodes;
	}
	
	public void testConnectionSuccess() {
		
		Exception ex = null;
		
		ClientLogic kvClient = new ClientLogic(this);
		try {
			kvClient.connect("localhost", 50001, false);
		} catch (Exception e) {
			ex = e;
		}	
		
		assertNull(ex);
	}
	
	
	public void testUnknownHost() {
		Exception ex = null;
		ClientLogic kvClient = new ClientLogic(this);
		try {
			kvClient.connect("unknown", 50001, false);
		} catch (Exception e) {
			ex = e;
		}	
		
		assertTrue(ex instanceof UnknownHostException);
	}
	
	
	public void testIllegalPort() {
		Exception ex = null;
		ClientLogic kvClient = new ClientLogic(this);
		try {
			kvClient.connect("localhost", 123456789, false);
		} catch (Exception e) {
			ex = e;
		}
		
		assertTrue(ex instanceof IllegalArgumentException);
	}
	
	

	
}

