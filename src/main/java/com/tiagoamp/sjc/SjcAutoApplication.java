package com.tiagoamp.sjc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.ConfigurationException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tiagoamp.sjc.model.ExpirationManager;
import com.tiagoamp.sjc.service.UploadService;

@SpringBootApplication
public class SjcAutoApplication {
	
	public static Path BASE_DIR;
	
	
	public static void main(String[] args) {		
		String userDirectory = System.getProperty("user.dir");
		BASE_DIR = Paths.get(userDirectory);
		Path CONFIG_FILE = BASE_DIR.resolve("resources" + File.separator + "conf.dat");
				
		ExpirationManager expirationManager = new ExpirationManager();
		
		try {
			Integer expirationValue = expirationManager.loadValueFromConfigFile(CONFIG_FILE);
			boolean isValid = expirationManager.checkExpiration(expirationValue);
			
			if (!isValid) {
				expirationManager.printExpiredMessage();
				waitInSeconds(3);
				System.out.println("Encerrado!");
				System.exit(1);
			}
			expirationManager.printValidMessage();
			
			createMissingDirectories(BASE_DIR.resolve("upload/"), BASE_DIR.resolve("resultado/"));
			
			UploadService uploadService = new UploadService();
			uploadService.cleanDirectory(BASE_DIR.resolve("upload/"));
			
		} catch (ConfigurationException e) {			
			e.printStackTrace();
			expirationManager.printConfigFileErrorMessage();
			waitInSeconds(3);
			System.out.println("Encerrado!");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erro ao acessar diretórios ('uploads' e/ou 'resultados') !!!");
			waitInSeconds(3);
			System.exit(1);
		}
		
		// Up the Web App
		SpringApplication.run(SjcAutoApplication.class, args);	
		
		System.out.println("---");
		System.out.println("*** System deployed! URL ==> http://localhost:8090/");
	}
	
	
	private static void createMissingDirectories(Path... paths) throws IOException {
		for (int i = 0; i < paths.length; i++) {
			if (Files.notExists(paths[i])) {
				Files.createDirectories(paths[i]);
			}
		}		
	}
	
	private static void waitInSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	 
}
