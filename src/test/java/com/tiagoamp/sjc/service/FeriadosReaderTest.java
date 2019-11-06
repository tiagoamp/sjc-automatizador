package com.tiagoamp.sjc.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FeriadosReaderTest {

	private FeriadosReader feriadosReader;
	
	@Test
	public void testGetHolidays_setterInput() {
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
	
	@Test
	public void testGetHolidays_fileInput() throws IOException {
		// given
		feriadosReader = new FeriadosReader();
		Path filePath = Paths.get("resources","feriados.txt");
		feriadosReader.loadFromFile(filePath);
		// when
		List<LocalDate> holidays = feriadosReader.getHolidays();
		holidays.forEach(System.out::println);
		// then
		assertNotNull(holidays);
		assertTrue(holidays.size() > 0);
	}

}
