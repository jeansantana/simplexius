package br.com.simplexius.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHelper {

	public static FileHelper instance = null;

	private FileHelper() {

	}

	public static FileHelper getInstance() {
		if (instance == null) {
			instance = new FileHelper();
		}
		return instance;
	}

	public String readFile(String path) throws IOException {
		String text = "Read Problem!";

		text = new String(Files.readAllBytes(Paths.get(path)));

		return text;
	}

	public void writeFile(String path, String text) throws IOException {
		Files.write(Paths.get(path), text.getBytes());
	}

}
