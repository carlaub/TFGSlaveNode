package application;

import model.SNInformation;

/**
 * Created by Carla Urrea Blázquez on 04/05/2018.
 *
 * SlaveNode.java
 *
 * Singleton class that contains the basic information that the SlaveNode need during the execution
 */
public class SlaveNode {
	private static SlaveNode instance;
	private SNInformation SNInformation;
	private int id;

	public static SlaveNode getInstance() {
		if (instance == null) {
			instance = new SlaveNode();
		}

		return instance;
	}

	public SlaveNode() {
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
}
