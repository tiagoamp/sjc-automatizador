package com.tiagoamp.sjc.service;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FeriadosReaderTest {

	private FeriadosReader feriadosReader;
	
	@Test
	public void testGetHolidays() {
		// given
		feriadosReader = new FeriadosReader();
		List<String> lines = Arrays.asList("25/12", "15/11", "05/05/1900", "12/12/1900");
		feriadosReader.setLines(lines);
		// when
		List<LocalDate> holidays = feriadosReader.getHolidays();
		// then 
		holidays.forEach(feriado -> {
			if (feriado.getYear() == 1900) {
				assertTrue(feriado.isEqual(LocalDate.of(1900, 05, 05)) || feriado.isEqual(LocalDate.of(1900, 12, 12)));
			} else {
				assertTrue(feriado.getDayOfMonth() == 25 || feriado.getDayOfMonth() == 15);
			}
		});
	}

}
