package com.tiagoamp.sjc.model;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class ExpirationManagerTest {
	
	private ExpirationManager expirationManager = new ExpirationManager();

	@Test
	public void testCheckExpiration_correctValue_shouldReturnTrue() {
		Integer currExpirationNumber = getCurrentExpirationNumber();
		boolean result = expirationManager.checkExpiration(currExpirationNumber);
		assertTrue(result);
	}
	
	@Test
	public void testCheckExpiration_wrongValue_shouldReturnFalse() {
		Integer wrongExpirationNumber = 1;
		boolean result = expirationManager.checkExpiration(wrongExpirationNumber);
		assertFalse(result);
	}

		
	private Integer getCurrentExpirationNumber() {
		LocalDate now = LocalDate.now();
		int sumOfYearPlusMonth = now.getYear() + now.getMonthValue();		
		return now.getMonthValue() % 2 == 0 ? sumOfYearPlusMonth * 30 : sumOfYearPlusMonth * 40;
	}
		
}
