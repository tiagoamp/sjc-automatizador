package com.tiagoamp.sjc.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import javax.naming.ConfigurationException;

import com.tiagoamp.sjc.SjcAutoApplication;

public class ExpirationManager {
	
	private final Path CONFIG_FILE = SjcAutoApplication.BASE_DIR.resolve("resources" + File.separator + "conf.dat");
	
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
		LocalDate now = LocalDate.now();
		int yearmonth = now.getYear() + now.getMonthValue();
		Integer result = null;
		if (now.getMonthValue() % 2 == 0) {
			result = yearmonth * 30;
		} else {
			result = yearmonth * 40;
		}
		return result;
	}
	
	private boolean isExpired(Integer input) {
		return input.intValue() != computeExpirationNumber().intValue();
	}

}
