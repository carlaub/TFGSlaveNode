package adapter;

import application.SlaveNode;
import constants.ErrorConstants;
import constants.GenericConstants;
import hadoop.HadoopUtils;

import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.DynamicRelationshipType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carla Urrea Blázquez on 08/05/2018.
 *
 * Neo4JImport.java
 *
 * This class contains the methods needed for import the graph partition into Neo4J.
 * The finality is interpret the partition files stored in HDFS, parse it and create the Neo4J DB for this partition
 */
public class Neo4JImport {
	private BatchInserter batchInserter;
	Map<Integer, Long> nodeCache;

	public Neo4JImport() {
		nodeCache = new HashMap<Integer, Long>();
	}

	public boolean startPartitionDBImport() {
		// Init Neo4j batchinserter
		if (!initBatchInserter()) {
			System.out.println(ErrorConstants.ERR_INIT_BATCHINSERTER);
		}

		// Process partition file of nodes
		if (!processNodesPartitionFile()) {
			System.out.println(ErrorConstants.ERR_PARSE_NODE_PARTITION_FILE);
			return false;
		}

		// Process partition file of edges
		/*if (!processEdgesPartitionFile()) {
			System.out.println(ErrorConstants.ERR_PARSE_NODE_PARTITION_FILE);
			return false;
		}*/

		batchInserter.shutdown();


		return true;
	}

	private boolean initBatchInserter() {
		try {
			//NEO4J BATCHINSERTER Configuration
			batchInserter = BatchInserters.inserter(new File(SlaveNode.getInstance().getSNInformation().getNeo4jDBPath()));
			if (batchInserter != null) {
				SlaveNode.getInstance().setBatchInserter(batchInserter);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Node line format:
	 * [id	labelsNum	label1	labelN	Attribute1Name	Attribute1Value	AttributeNName	AttributeNValue]
	 * @param line
	 * @return
	 */
	private boolean createNode(String line) {
		int labelsNum;
		int index = 0;
		int id;
		long neo4jId;
		Label[] labels;

		String[] parts = line.split("\t");

		// DEBUG
		for(String part : parts) {
			System.out.println("PART: " + part);
		}

		int totalParts = parts.length;

		Map<String, Object> properties = new HashMap<String, Object>();
		id = Integer.parseInt(parts[index]);
		index ++;
		properties.put("id", id);

		// Labels processing
		labelsNum = Integer.parseInt(parts[index]);
		index++;
		labels = new Label[labelsNum];
		for (int i = 0; i < labelsNum; i++) {
			labels[i] = DynamicLabel.label(parts[index]);
			index++;
		}

		// TODO: CAMBIAR ID, UTILIZAR INDEX MANAGER  index() (https://neo4j.com/docs/java-reference/current/javadocs/org/neo4j/graphdb/GraphDatabaseService.html#createNode--)
		// Attributes processing
		while(index < totalParts) {
			properties.put(parts[index], parts[index + 1]);
			index += 2;
		}

		neo4jId = batchInserter.createNode(properties, labels);
		if (neo4jId >= 0) {
			nodeCache.put(id, neo4jId);
			return true;
		}
		return false;
	}

	private boolean processNodesPartitionFile() {
		String line;
		BufferedReader br = HadoopUtils.getInstance().getBufferReaderHFDSFile(SlaveNode.getInstance().getSNInformation().getHDFSWorkingDirectory() + GenericConstants.FILE_NAME_NODES_PARTITION_BASE + SlaveNode.getInstance().getId() + ".txt");

		if (br == null) return false;

		System.out.println("READING FILE: " + GenericConstants.FILE_NAME_NODES_PARTITION_BASE + SlaveNode.getInstance().getId() + "\n\n");
		try {
			while((line = br.readLine()) != null) {
				System.out.println(line);
				createNode(line);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private void createRelation(String line) {
		int index = 0;
		int fromNode;
		int toNode;

		Map<String, Object> properties = new HashMap<String, Object>();

		String[] parts = line.split("\t");
		int numParts = parts.length;

		fromNode = Integer.parseInt(parts[0]);
		toNode = Integer.parseInt(parts[1]);

		// Relationship type
		RelationshipType type = DynamicRelationshipType.withName(parts[2]);

		for (int i = 3; i < numParts; i = i + 2) {
			properties.put(parts[i], parts[i + 1]);
		}

		// Relationship properties processing
		batchInserter.createRelationship(nodeCache.get(fromNode), nodeCache.get(toNode), type, properties);
	}

	private boolean processEdgesPartitionFile() {
		String line;
		BufferedReader br = HadoopUtils.getInstance().getBufferReaderHFDSFile(SlaveNode.getInstance().getSNInformation().getHDFSWorkingDirectory() + GenericConstants.FILE_NAME_EDGES_PARTITION_BASE + SlaveNode.getInstance().getId() + ".txt");

		if (br == null) return false;

		System.out.println("READING FILE: " + GenericConstants.FILE_NAME_EDGES_PARTITION_BASE + SlaveNode.getInstance().getId() + "\n\n");
		try {
			while((line = br.readLine()) != null) {
				System.out.println(line);
				createRelation(line);
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}
