package com.atum.game;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class Configuration {
	
	public int port;
	
	public int getPort(){
		return port;
	}

	public static Configuration loadServerConfig() {
		YamlReader reader;
		try {
			reader = new YamlReader(new FileReader("data/config.yml"));
			Configuration config = reader.read(Configuration.class);
			return config;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (YamlException e) {
			e.printStackTrace();
		}
		return null;
	}

}
