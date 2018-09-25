package network;

/**
 * Created by Carla Urrea Blázquez on 24/06/2018.
 *
 * NetworkConstants.java
 */
public class NetworkConstants {

	// Packet code
	public static final int PCK_CODE_ID = 10; // MM send id to SN
	static final int PCK_CODE_START_DB = 11; // MM send to SN the order of mount the partition assigned to this node
	static final int PCK_STATUS_OK_START_DB = 12;
	static final int PCK_STATUS_KO_START_DB = 13;
	static final int PCK_DISCONNECT = 40; // Disconnect nodes from the system
	static final int PCK_QUERY = 50; // The packet contains a query
	static final int PCK_QUERY_RESULT = 51; // The packet contains a query's result
}

