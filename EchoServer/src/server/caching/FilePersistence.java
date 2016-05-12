package server.caching;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import server.models.Server;

public class FilePersistence {
	private Logger LOGGER = Logger.getLogger(FilePersistence.class);
	BufferedWriter bw;
	File file;

	public FilePersistence() {
		file = new File("../data.txt");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				LOGGER.error("Error ! error occured while creating file", ex);

			}
		}
	}

	public void write(String key, String value) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(key + "," + value + "\n");
			fw.close();
			bw.close();
		} catch (IOException ex) {
			LOGGER.error("Error ! error occured while writing to file", ex);
		}
	}

	public String read(String key) {
		String line;
		String value = null;
		int index;
		FileReader fr = null;
		try {
			fr = new FileReader(file);

			BufferedReader reader = new BufferedReader(fr);

			while ((line = reader.readLine()) != null) {
				index = line.indexOf(",");
				if (index != -1)
					if (key.equals(line.substring(0, index))) {
						value = line.substring(index + 1, line.length());

					}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("Error ! error occured while reading from file ", e);
		} catch (IOException e) {
			LOGGER.error("Error ! error occured while reading from file ", e);
		}
		return value;

	}
}
