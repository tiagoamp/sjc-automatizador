package com.tiagoamp.sjc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SjcAutoApplication {

	
	//FIXME
	public static Path BASE_DIR = null;
		
	public static void main(String[] args) {
		
		String userDirectory = System.getProperty("user.dir");
		BASE_DIR = Paths.get(userDirectory);
		
		try {
			cleanUploadDirectory();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erro ao limpar diretório de uploads!!!");
			System.exit(1);
		}
				
		SpringApplication.run(SjcAutoApplication.class, args);		
	}

	
	private static void cleanUploadDirectory() throws IOException {
		Files.newDirectoryStream(BASE_DIR.resolve("upload/")).forEach( f -> {
			try {
				Files.delete(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
