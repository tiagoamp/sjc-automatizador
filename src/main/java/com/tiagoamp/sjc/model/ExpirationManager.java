package com.tiagoamp.sjc.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import javax.naming.ConfigurationException;

import com.tiagoamp.sjc.SjcAutoApplication;

public class ExpirationManager {
	
	private final Path CONFIG_FILE = SjcAutoApplication.BASE_DIR.resolve("conf.dat");
	
	public Boolean checkExpiration() throws ConfigurationException, IOException {
		byte[] bytesArr = Files.readAllBytes(CONFIG_FILE);
		String text = new String(bytesArr);
		
		if (text == null || text.isEmpty()) throw new ConfigurationException();
		text = text.replaceAll("\\D", ""); // only numbers
		Integer input = Integer.valueOf(text);
		
		if (isExpired(input)) {
			return false;
		}
		return true;
	}
	
	private Integer computeExpirationNumber() {
		int year = LocalDate.now().getYear();
		Integer result = null;
		if (year % 2 == 0) {
			result = year * 30;
		} else {
			result = year * 40;
		}
		return result;
	}
	
	private boolean isExpired(Integer input) {
		return input.intValue() != computeExpirationNumber().intValue();
	}

}
