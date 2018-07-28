package network;

import neo4j.*;
import application.SlaveNode;
import constants.GenericConstants;
import org.neo4j.graphdb.Result;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

/**
 * Created by Carla Urrea Blázquez on 05/05/2018.
 * <p>
 * SNClient.java
 * <p>
 * Class that manage the communication with the server
 */
public class SNClient {
	private final int MMPort;
	private DatagramPacket dPacket;
	private DatagramSocket dSocket;
	private InetAddress ipAdress;
	byte[] buff;

	public SNClient() {
		MMPort = SlaveNode.getInstance().getSNInformation().getMMPort();

		try {
			dSocket = new DatagramSocket();
			ipAdress = InetAddress.getByName(SlaveNode.getInstance().getSNInformation().getMMIp());
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function implements the communication protocol with the MM.
	 * <p>
	 * First of all, SlaveNode send a initial packet to the server. Server responds with the ID assigned to this SlaveNode.
	 * Second, the slave node wait for establish the DB, with the partition assigned to it. The slave node responds with the status
	 * of the process.
	 * If all is correct, the node will waiting until a query arrives.
	 */
	public void startCommunication() {
		Msg msgFromServer;
		boolean exit = false;

		connectToServer();

		// Bucle waiting request from server
		while (!exit) {
			msgFromServer = waitFromServer();

			switch (msgFromServer.getCode()) {
				case NetworkConstants.PCK_CODE_START_DB:
					// Partitions files have been created and saved in HDFS
					Neo4JImport neo4JImport = new Neo4JImport();
					if (neo4JImport.startPartitionDBImport()) {
						sendPacketToServer(new Msg(NetworkConstants.PCK_STATUS_OK_START_DB, null));
					} else {
						sendPacketToServer(new Msg(NetworkConstants.PCK_STATUS_KO_START_DB, null));
					}
					break;

				case NetworkConstants.PCK_DISCONNECT:
					// Disconnect from the system
					SlaveNode.getInstance().shutDownSlaveNode();
					GraphDatabase.getInstance().shutdown();
					System.out.println("Bye!");
					exit = true;
					break;

				case NetworkConstants.PCK_QUERY:
					System.out.println("NEW QUERY RECEIVED");
					ResultQuery result = QueryExecutor.getInstace().processQuery(msgFromServer.getDataAsString());

					sendPacketToServer(new Msg(NetworkConstants.PCK_QUERY_RESULT, result));

					break;
			}
		}
	}

	private void connectToServer() {

		Msg msg = new Msg(12, "Hello");
		sendPacketToServer(msg);

		// Server send the ID assigned to this node
		Msg response = waitFromServer();
		SlaveNode.getInstance().setId(Integer.valueOf(response.getDataAsString()));
	}

	private Msg waitFromServer() {
		Msg msg = null;
		try {
			byte[] dataIncome = new byte[65535];
			DatagramPacket dPacketIncome = new DatagramPacket(dataIncome, dataIncome.length);
			dSocket.receive(dPacketIncome);

			ByteArrayInputStream baos = new ByteArrayInputStream(dPacketIncome.getData());
			ObjectInputStream ois = new ObjectInputStream(baos);

			msg = (Msg) ois.readObject();
			System.out.println("New Message!");
			System.out.println("Code: " + msg.getCode() + "  Data: " + msg.getDataAsString());

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return msg;
	}


	private void sendPacketToServer(Msg msg) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;

			oos = new ObjectOutputStream(baos);
			oos.writeObject(msg);
			byte[] data = baos.toByteArray();

			dPacket = new DatagramPacket(data, data.length, ipAdress, MMPort);
			dSocket.send(dPacket);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
