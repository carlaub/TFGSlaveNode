package hadoop;
import application.SlaveNode;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;

/**
 * Created by Carla Urrea Blázquez on 26/04/2018.
 *
 * HadoopUtils.java
 */
public class HadoopUtils {
	private static HadoopUtils instance;
	private FileSystem fs;

	public HadoopUtils() {
		configureHadoop();
	}

	public static HadoopUtils getInstance() {
		if (instance == null) {
			instance = new HadoopUtils();
		}

		return instance;
	}

	private void configureHadoop() {
		Configuration configuration = new Configuration();
		//Required by Maven
		configuration.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		configuration.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		configuration.set("dfs.replication", "1");

		configuration.set("fs.defaultFS", SlaveNode.getInstance().getSNInformation().getDefaultFS());
		try {
			fs = FileSystem.get(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the BufferReader of the HDFS file following the path *filePath*
	 *
	 * @param filePath Complete file's path/url into HDFS with
	 *                 Ex format: hdfs://node1-master:9000/user/hadoop/people.txt
	 * @return
	 */
	public BufferedReader getBufferReaderHFDSFile(String filePath) {
		Path path = new Path(filePath);
		try {
			FSDataInputStream fsDataInputStream = fs.open(path);
			return new BufferedReader(new InputStreamReader(fsDataInputStream));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void closeResources() {
		 try {
			 if (fs != null) fs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}