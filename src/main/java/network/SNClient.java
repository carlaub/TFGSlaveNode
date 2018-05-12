package network;

import application.SlaveNode;
import constants.GenericConstants;
import hadoop.HadoopUtils;

import java.io.*;
import java.net.*;

/**
 * Created by Carla Urrea Blázquez on 05/05/2018.
 *
 * SNClient.java
 *
 * Class that manage the communication with the server
 */
public class SNClient {
	DatagramPacket dPacket;
	DatagramSocket dSocket;
	InetAddress ipAdress;
	byte[] buff;

	public SNClient() {
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
	 *
	 * First of all, SlaveNode send a initial packet to the server. Server responds with the ID assigned to this SlaveNode.
	 * Second, the slave node wait for establish the DB, with the partition assigned to it. The slave node responds with the status
	 * of the process.
	 * If all is correct, the node will waiting until a query arrives.
	 *
	 */
	public void startCommunication() {
		Msg msgFromServer;
		boolean exit = false;

		connectToServer();

		// Bucle waiting request from server
		while (!exit) {
			msgFromServer = waitFromServer();

			switch(msgFromServer.getCode()) {
				case GenericConstants.PCK_CODE_START_DB:
					// Partitions files have been created and saved in HDFS
					break;

				case GenericConstants.PCK_DISCONNECT:
					// Disconnect from the system
					// TODO: Disconnect Neo4j DB. Clean
					HadoopUtils.getInstance().closeResources();
					System.out.println("Bye!");
					exit = true;
					break;
			}
		}

	}

	public void connectToServer() {

		try {
			Msg msg = new Msg(12, "Hello");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(msg);
			byte[] data = baos.toByteArray();

			dPacket = new DatagramPacket(data, data.length, ipAdress, 3456);

			dSocket.send(dPacket);

			// Server send the ID assigned to this node
			Msg response = waitFromServer();
			SlaveNode.getInstance().setId(Integer.valueOf(response.getData()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Msg waitFromServer() {
		Msg msg = null;
		try {
			byte[] dataIncome = new byte[65535];
			DatagramPacket dPacketIncome = new DatagramPacket(dataIncome, dataIncome.length);
			dSocket.receive(dPacketIncome);

			ByteArrayInputStream baos = new ByteArrayInputStream(dPacketIncome.getData());
			ObjectInputStream ois = new ObjectInputStream(baos);

			msg = (Msg) ois.readObject();
			System.out.println("New Message!");
			System.out.println("Code: " + msg.getCode() + "  Data: " + msg.getData()) ;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return msg;
	}
}
