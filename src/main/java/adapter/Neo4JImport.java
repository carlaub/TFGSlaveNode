package adapter;

import application.SlaveNode;
import constants.ErrorConstants;
import constants.GenericConstants;
import hadoop.HadoopUtils;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by Carla Urrea Blázquez on 08/05/2018.
 *
 * Neo4JImport.java
 *
 * This class contains the methods needed for import the graph partition into Neo4J.
 * The finality is interpret the partition files stored in HDFS, parse it and create the Neo4J DB for this partition
 */
public class Neo4JImport {

	public boolean startPartitionDBImport() {
		// Process partition file of nodes
		if (!processNodesPartitionFile()) {
			System.out.println(ErrorConstants.ERR_PARSE_NODE_PARTITION_FILE);
			return false;
		}

		// Process partition file of edges
		if (!processEdgesPartitionFile()) {
			System.out.println(ErrorConstants.ERR_PARSE_NODE_PARTITION_FILE);
			return false;
		}

		return true;
	}

	private boolean processNodesPartitionFile() {
		String line;
		BufferedReader br = HadoopUtils.getInstance().getBufferReaderHFDSFile(SlaveNode.getInstance().getSNInformation().getHDFSWorkingDirectory() + GenericConstants.FILE_NAME_NODES_PARTITION_BASE + SlaveNode.getInstance().getId());

		if (br == null) return false;

		System.out.println("READING FILE: " + GenericConstants.FILE_NAME_NODES_PARTITION_BASE + SlaveNode.getInstance().getId() + "\n\n");
		try {
			while((line = br.readLine()) != null) {
				System.out.println(line);

			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean processEdgesPartitionFile() {
		String line;
		BufferedReader br = HadoopUtils.getInstance().getBufferReaderHFDSFile(SlaveNode.getInstance().getSNInformation().getHDFSWorkingDirectory() + GenericConstants.FILE_NAME_EDGES_PARTITION_BASE + SlaveNode.getInstance().getId());

		if (br == null) return false;

		System.out.println("READING FILE: " + GenericConstants.FILE_NAME_EDGES_PARTITION_BASE + SlaveNode.getInstance().getId() + "\n\n");
		try {
			while((line = br.readLine()) != null) {
				System.out.println(line);

			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}
