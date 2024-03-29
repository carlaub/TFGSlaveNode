package network;

import application.SlaveNode;
import neo4j.GraphDatabase;
import neo4j.Neo4JImport;
import neo4j.QueryExecutor;
import neo4j.ResultQuery;

import java.io.*;
import java.net.*;

/**
 * Created by Carla Urrea Blázquez on 05/05/2018.
 *
 * Class that manage the communication with the server
 */
public class SNClient {
	private final int MMPort;
	private DatagramSocket dSocket;
	private InetAddress ipAdress;

	public SNClient() {
		MMPort = SlaveNode.getInstance().getSNInformation().getMMPort();

		try {
			dSocket = new DatagramSocket();
			ipAdress = InetAddress.getByName(SlaveNode.getInstance().getSNInformation().getMMIp());
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function implements the communication protocol with the MM.
	 *
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

					QueryExecutor.getInstace().processQuery("MATCH (n{id: 5}) RETURN n.name;");
					break;

				case NetworkConstants.PCK_DISCONNECT:
					// Disconnect from the system
					SlaveNode.getInstance().shutDownSlaveNode();
					GraphDatabase.getInstance().shutdown();
					System.out.println("Bye!");
					exit = true;
					break;

				case NetworkConstants.PCK_QUERY:
					System.out.println("-> New query received");
					ResultQuery result = QueryExecutor.getInstace().processQuery(msgFromServer.getDataAsString());

					sendPacketToServer(new Msg(NetworkConstants.PCK_QUERY_RESULT, result));

					break;
			}
		}
	}

	/**
	 * This function connects the SN to the MM through the package with code 12.
	 */
	private void connectToServer() {
		Msg msg = new Msg(12, "Hello");
		sendPacketToServer(msg);

		// Server send the ID assigned to this node
		Msg response = waitFromServer();
		SlaveNode.getInstance().setId(Integer.valueOf(response.getDataAsString()));
	}

	/**
	 * Wait response from the server.
	 * @return the msg that que server, MM, sent to the SN.
	 */
	private Msg waitFromServer() {
		Msg msg = null;
		try {
			byte[] dataIncome = new byte[65535];
			DatagramPacket dPacketIncome = new DatagramPacket(dataIncome, dataIncome.length);
			dSocket.receive(dPacketIncome);

			ByteArrayInputStream baos = new ByteArrayInputStream(dPacketIncome.getData());
			ObjectInputStream ois = new ObjectInputStream(baos);

			msg = (Msg) ois.readObject();
			System.out.println("-> New Message - ");
			System.out.print("	Code: " + msg.getCode() + "  Data: " + msg.getDataAsString()+"\n");

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return msg;
	}

	/**
	 * Send the message [msg] to the server MM.
	 * @param msg structure with code and data that contains the information that want to be send.
	 */
	private void sendPacketToServer(Msg msg) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;

			oos = new ObjectOutputStream(baos);
			oos.writeObject(msg);
			byte[] data = baos.toByteArray();

			DatagramPacket dPacket = new DatagramPacket(data, data.length, ipAdress, MMPort);
			dSocket.send(dPacket);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
