package testing;

import java.util.ArrayList;

import org.junit.Test;

import client.models.ClientLogic;
import client.models.MetaInfoHolder;
import commons.exceptions.InvalidParameterException;
import commons.metaData.ServerInfo;

import junit.framework.TestCase;

public class AdditionalTest extends TestCase implements MetaInfoHolder {
	private ServerInfo connectedServer = null;
	private ArrayList<ServerInfo> runningServerNodes = null;
	private ClientLogic kvClient;

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

	public void setUp() {
		kvClient = new ClientLogic(this);
		try {
			kvClient.connect("localhost", 50001, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void tearDown() {
		kvClient.disconnect();
	}

	

	@Test
	public void testValueNullInPut() {
		String key = "foo1";
		String value = null;
		Exception ex = null;

		try {
			kvClient.put(key, value);
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex instanceof InvalidParameterException);
	}

	@Test
	public void testKeyIsTooLargeInPut() {
		String key = "foo01234567890123456789";
		String value = "bar3";
		Exception ex = null;

		try {
			kvClient.put(key, value);
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex instanceof InvalidParameterException);
	}

	@Test
	public void testKeyNullInPut() {
		String key = null;
		String value = "bar2";
		Exception ex = null;

		try {
			kvClient.put(key, value);
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex instanceof InvalidParameterException);
	}
}
