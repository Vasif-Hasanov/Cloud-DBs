package testing;

import java.util.ArrayList;

import org.junit.Test;
import commons.data.KVMessage;
import commons.data.MessageStatusType;
import commons.metaData.ServerInfo;
import client.models.ClientLogic;
import client.models.MetaInfoHolder;
import junit.framework.TestCase;


public class InteractionTest extends TestCase implements MetaInfoHolder{
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
	public void testPut() {
		String key = "foo1";
		String value = "bar1";
		KVMessage response = null;
		Exception ex = null;

		try {
			response = kvClient.put(key, value);
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.status == MessageStatusType.PUT_SUCCESS);
	}
	
	@Test
	public void testPutDisconnected() {
		kvClient.disconnect();
		String key = "foo2";
		String value = "bar2";
		Exception ex = null;

		try {
			kvClient.put(key, value);
		} catch (Exception e) {
			ex = e;
		}

		assertNotNull(ex);
	}

	@Test
	public void testUpdate() {
		String key = "updateTestValue";
		String initialValue = "initial";
		String updatedValue = "updated";
		
		KVMessage response = null;
		Exception ex = null;

		try {
			kvClient.put(key, initialValue);
			response = kvClient.put(key, updatedValue);			
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.status == MessageStatusType.PUT_UPDATE
				&& response.value.equals(updatedValue));
	}
	
	@Test
	public void testDelete() {
		String key = "deleteTestValue";
		String value = "toDelete";
		
		KVMessage response = null;
		Exception ex = null;

		try {
			kvClient.put(key, value);
			response = kvClient.put(key, "null");
			
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.status == MessageStatusType.DELETE_SUCCESS);
	}
	
	@Test
	public void testGet() {
		String key = "foo3";
		String value = "bar3";
		KVMessage response = null;
		Exception ex = null;

			try {
				kvClient.put(key, value);
				response = kvClient.get(key);
			} catch (Exception e) {
				ex = e;
			}
		
		assertTrue(ex == null && response.value.equals(value));
	}

	@Test
	public void testGetUnsetValue() {
		String key = "an unset value";
		KVMessage response = null;
		Exception ex = null;

		try {
			response = kvClient.get(key);
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.status == MessageStatusType.GET_ERROR);
	}
	


}
