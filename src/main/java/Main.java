import application.SlaveNode;
import constants.ErrorConstants;
import model.SNInformation;
import network.SNClient;
import util.ParserConf;

import java.io.IOException;

/**
 * Created by Carla Urrea Blázquez Mon 04/05/2018.
 *
 * Main.java
 */
public class Main {
	public static void main(String[] args) {
		System.out.println("/************* SLAVE NODE *************/\n\n");

		// Create application singleton object
		SlaveNode slaveNode = SlaveNode.getInstance();

		// Read SlaveNode configuration file
		ParserConf parserConf = new ParserConf();

		try {
			SNInformation snInformation = parserConf.getConfiguration();
			if (snInformation != null) {
				slaveNode.setSNInformation(snInformation);
			} else {
				// Error parsing configuration file
				System.out.println(ErrorConstants.errConfigurationFileParser);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		SNClient snClient = new SNClient();
		snClient.startCommunication();
	}
}
