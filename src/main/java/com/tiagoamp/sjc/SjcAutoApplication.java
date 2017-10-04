package com.tiagoamp.sjc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.ConfigurationException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tiagoamp.sjc.model.ExpirationManager;
import com.tiagoamp.sjc.service.UploadService;

@SpringBootApplication
public class SjcAutoApplication {

	public static Path BASE_DIR = null;
	
	
	public static void main(String[] args) {
		
		String userDirectory = System.getProperty("user.dir");
		BASE_DIR = Paths.get(userDirectory);
		
		ExpirationManager config = new ExpirationManager();		
		try {
			boolean isValid = config.checkExpiration();
			if (!isValid) {
				System.out.println(" ==================================================== ");
				System.out.println("             !!! SISTEMA EXPIRADO !!!");
				System.out.println(" ==================================================== ");
				System.out.println("   Entre em contato com o administrador do sistema. ");
				System.out.println(" ==================================================== ");
				System.exit(1);
			}
			System.out.println("Configuration ok!");
		} catch (ConfigurationException | IOException e1) {			
			System.out.println(" =========================================== ");
			System.out.println(" !!! Arquivo de configuração corrompido !!!");
			System.out.println(" =========================================== ");
			System.exit(1);
		}
		
		try {
			UploadService uploadService = new UploadService();
			uploadService.cleanDirectory(BASE_DIR.resolve("upload/"));
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erro ao limpar diretório de uploads!!!");
			System.exit(1);
		}
		
		// Up the Web App
		SpringApplication.run(SjcAutoApplication.class, args);	
		
		System.out.println("---");
		System.out.println("*** System deployed ==> http://localhost:8090/");
	}
	 
}
