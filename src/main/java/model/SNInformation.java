package model;

/**
 * Created by Carla Urrea Blázquez on 04/05/2018.
 *
 * SNInformation.java
 *
 * Slave node configuration information
 */
public class SNInformation {
	private String MMIp;
	private int MMPort;
	private String defaultFS;
	private String HDFSWorkingDirectory;
	private String neo4jDBPath;


	public String getMMIp() {
		return MMIp;
	}

	public void setMMIp(String MMIp) {
		this.MMIp = MMIp;
	}

	public String getDefaultFS() {
		return defaultFS;
	}

	public void setDefaultFS(String defaultFS) {
		this.defaultFS = defaultFS;
	}

	public String getHDFSWorkingDirectory() {
		return HDFSWorkingDirectory;
	}

	public void setHDFSWorkingDirectory(String HDFSWorkingDirectory) {
		this.HDFSWorkingDirectory = HDFSWorkingDirectory;
	}

	public String getNeo4jDBPath() {
		return neo4jDBPath;
	}

	public void setNeo4jDBPath(String neo4jDBPath) {
		this.neo4jDBPath = neo4jDBPath;
	}

	public int getMMPort() {
		return MMPort;
	}

	public void setMMPort(int MMPort) {
		this.MMPort = MMPort;
	}
}
