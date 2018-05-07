package util;

import model.SNInformation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Carla Urrea Blázquez on 04/05/2018.
 *
 * ParserConf.java
 *
 * This class is a parser for the config file of the MetadataManager
 *
 * The config file format:
 *	-> MetadataManager IP
 *
 */
public class ParserConf {
	BufferedReader brConfiFile;

	public ParserConf() {
		try {
			brConfiFile = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/src/main/resources/files/SlaveNode.conf"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public SNInformation getConfiguration() throws IOException {
		SNInformation snInformation = new SNInformation();

		// Read MetadataManager's IP
		snInformation.setMMIp(brConfiFile.readLine());

		printConfigurationInformation(snInformation);

		return snInformation;
	}

	private void printConfigurationInformation(SNInformation snInformation) {
		System.out.println("MetadataManager IP: " + snInformation.getMMIp());
	}

}
