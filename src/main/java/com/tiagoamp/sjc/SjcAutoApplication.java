package com.tiagoamp.sjc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SjcAutoApplication {

	public static Path UPLOAD_DIR = Paths.get("/home/d333280/Ti/proj/SJC/sjc-automatizador/upload/");
	
	
	public static void main(String[] args) {
		
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
		Files.newDirectoryStream(UPLOAD_DIR).forEach( f -> {
			try {
				Files.delete(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}
