package com.tiagoamp.sjc.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import javax.naming.ConfigurationException;

public class ExpirationManager {
		
	public Integer loadValueFromConfigFile(Path configFile) throws IOException, ConfigurationException {
		byte[] bytesArr = Files.readAllBytes(configFile);
		String text = new String(bytesArr);
		if (text == null || text.isEmpty()) throw new ConfigurationException("Valor não encontrado no arquivode configuração!");
		text = text.replaceAll("\\D", ""); // only numbers
		return Integer.valueOf(text);
	}	
	
	public boolean checkExpiration(Integer value) {
		Integer computedExpirationNumber = computeExpirationNumber();		
		return value.intValue() == computedExpirationNumber.intValue();
	}
	
	public void printExpiredMessage() {
		System.out.println(" ==================================================== ");
		System.out.println("             !!! SISTEMA EXPIRADO !!!");
		System.out.println(" ==================================================== ");
		System.out.println("   Entre em contato com o administrador do sistema. ");
		System.out.println(" ==================================================== ");
	}
	
	public void printValidMessage() {
		System.out.println("Configuration ok!");
	}
	
	public void printConfigFileErrorMessage() {
		System.out.println(" =========================================== ");
		System.out.println(" !!! Arquivo de configuração corrompido !!!");
		System.out.println(" =========================================== ");
	}
	
	
	private Integer computeExpirationNumber() {
		LocalDate now = LocalDate.now();
		int sumOfYearAndMonth = now.getYear() + now.getMonthValue();		
		return ???;
	}
	
}
