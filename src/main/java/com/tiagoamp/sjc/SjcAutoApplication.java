package com.tiagoamp.sjc;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tiagoamp.sjc.model.ExpirationManager;
import com.tiagoamp.sjc.service.FilesService;

@SpringBootApplication
public class SjcAutoApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SjcAutoApplication.class);	
	
	public static Path BASE_DIR, CONFIG_FILE, DIR_ENTRADA, DIR_SAIDA;
	private static FilesService filesService;
	
	
	public static void main(String[] args) {
		try {
			
			initializeWorkingDirectories();
			doExpirationValidation();
			filesService.createDirectories(DIR_ENTRADA, DIR_SAIDA);
			filesService.cleanDirectories(DIR_ENTRADA, DIR_SAIDA);
			
		} catch (Exception e) {
			System.exit(1);
		}
		
		// Up the Web App
		SpringApplication.run(SjcAutoApplication.class, args);	
		
		System.out.println("---");
		System.out.println("*** System deployed! URL ==> http://localhost:8090/");
		System.out.println("---");
	}
	
	
	
	
	private static void doExpirationValidation() throws Exception {
		ExpirationManager expirationManager = new ExpirationManager();		
		try {
			Integer expirationValue = expirationManager.loadValueFromConfigFile(CONFIG_FILE);
			boolean isValid = expirationManager.checkExpiration(expirationValue);
			
			if (!isValid) {
				expirationManager.printExpiredMessage();
				Thread.sleep(3 * 1000);
				System.out.println("Encerrado!");
				System.exit(1);
			}
			expirationManager.printValidMessage();
						
		} catch (ConfigurationException e) {			
			LOGGER.error("Application config error", e);
			expirationManager.printConfigFileErrorMessage();
			Thread.sleep(3 * 1000);
			System.out.println("Encerrado!");
			throw new Exception(e);
		}
	}
	 
}
