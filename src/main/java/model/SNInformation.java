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
	private String defaultFS;
	private String HDFSWorkingDirectory;


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
}
