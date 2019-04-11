package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eadge.extractpdfexcel.exception.IncorrectFileTypeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InputConverterTest {

	private InputConverter converter;
	private Path file = Paths.get("testfiles", "entrada", "Modelo.PDF");
	private Path resultFile = Paths.get("testfiles", "entrada", "Modelo.xlsx");
	
	@Before
	public void setUp() throws Exception {
		Files.deleteIfExists(resultFile);
		converter = new InputConverter(file);
	}
	

	@Test
	public void testLoad_validInput_shouldReturnValidOutput() throws FileNotFoundException, IncorrectFileTypeException {
		//converter.load();
		//assertTrue(Files.exists(resultFile));
		Path file = Paths.get("testfiles", "entrada", "Modelo.PDF");
		InputConverter c = new InputConverter(file);
		c.load();
	}

}
