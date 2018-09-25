package application;

import hadoop.HadoopUtils;
import model.SNInformation;
import org.neo4j.unsafe.batchinsert.BatchInserter;

/**
 * Created by Carla Urrea Blázquez on 04/05/2018.
 *
 * Singleton class that contains the basic information that the SlaveNode need during the execution
 */
public class SlaveNode {
	private static SlaveNode instance;
	private SNInformation SNInformation;
	private int id;
	private BatchInserter batchInserter;

	public static SlaveNode getInstance() {
		if (instance == null) {
			instance = new SlaveNode();
		}

		return instance;
	}

	private SlaveNode() {
		this.id = -1;
	}

	public model.SNInformation getSNInformation() {
		return SNInformation;
	}

	public void setSNInformation(model.SNInformation SNInformation) {
		this.SNInformation = SNInformation;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private BatchInserter getBatchInserter() {
		return batchInserter;
	}

	public void setBatchInserter(BatchInserter batchInserter) {
		this.batchInserter = batchInserter;
	}

	public void shutDownSlaveNode() {
		//Clean Hadoop
		HadoopUtils.getInstance().closeResources();
		// Shutdown Neo4J BatchInserter
		if (SlaveNode.getInstance().getBatchInserter() != null) {
			SlaveNode.getInstance().getBatchInserter().shutdown();
		}
	}
}
