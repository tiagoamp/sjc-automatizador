package com.tiagoamp.sjc.model;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

public class ExpirationManagerTest {
	
	private ExpirationManager expirationManager = new ExpirationManager();

	@Test
	public void testCheckExpiration_wrongValue_shouldReturnFalse() {
		Integer wrongExpirationNumber = 1;
		boolean result = expirationManager.checkExpiration(wrongExpirationNumber);
		assertFalse(result);
	}
	
}
