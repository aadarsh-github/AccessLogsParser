package com.unisys;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyFileReader {
	public String getProperty(String value) throws FileNotFoundException {
		InputStream in = new FileInputStream(Constants.CONFIG_FILE_PATH);
		Properties prop = new Properties();
		try {
			//System.out.println(in);
			prop.load(in);

			return prop.getProperty(value);
		} catch (IOException ex) {
			ex.printStackTrace();
			return Constants.EMPTY;
		}
	}	
}
