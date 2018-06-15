package com.tiagoamp.sjc.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;

public class SpreadsheetServicesTest {
	
	private SpreadsheetServices service;
	private static Path inputDir = Paths.get("testfiles", "entrada");
	private static Path outputDir = Paths.get("testfiles", "saida");

	@BeforeClass
	public static void init() throws IOException {
		if (Files.notExists(outputDir)) {
			Files.createDirectories(outputDir);
		}
	}
	
	@Before
	public void setUp() throws Exception {
		service = new SpreadsheetServices();		
	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test
	public void testLoadInputSpreadSheet_shouldLoadFile() throws IOException {
		InputSpreadsheet result = service.loadInputSpreadsheet(inputDir.resolve("template_input.xlsx"));
		assertNotNull("Must generate the input spreadsheet.", result);
	}
	
	@Test
	public void testLoadInputSpreadsheetsFromDirectory_shouldLoadFiles() throws IOException {
		List<InputSpreadsheet> result = service.loadInputSpreadsheetsFromDirectory(inputDir);
		assertNotNull("Must generate list of input spreadsheets.", result);
	}

	@Test
	public void testGenerateOutputSpreadSheet_shouldGenerateValidOutput() throws IOException {
		List<InputSpreadsheet> inputlist = service.loadInputSpreadsheetsFromDirectory(inputDir);
		OutputSpreadsheet result = service.generateOutputSpreadSheet(inputlist);
		assertNotNull("Must generate output spreadsheets.", result);
		
	}

	@Test
	public void testGenerateOuputSpreadsheetFile_shouldGenerateValidOutput() throws IOException {
		List<InputSpreadsheet> inputlist = service.loadInputSpreadsheetsFromDirectory(inputDir);
		OutputSpreadsheet outspreadsheet = service.generateOutputSpreadSheet(inputlist);
		Path outputfile = outputDir.resolve("testOutFromFacadeTest.xlsx");
		service.generateOuputSpreadsheetFile(outputfile, outspreadsheet, null);
		assertTrue("Must generate output spreadsheet file.", Files.exists(outputfile));
	}
	
}
