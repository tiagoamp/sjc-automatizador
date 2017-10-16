package br.com.tiagoamp.sjcservice.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.tiagoamp.sjcservice.model.input.InputSpreadsheet;
import br.com.tiagoamp.sjcservice.model.output.OutputSpreadsheet;

public class SjcServicesFacadeTest {
	
	private SjcServicesFacade facade;

	@Before
	public void setUp() throws Exception {
		facade = new SjcServicesFacade();
	}

	@After
	public void tearDown() throws Exception {
		facade = null;
	}

	@Test
	public void testLoadInputSpreadsheetsFromDirectory_shouldLoadFiles() throws IOException {
		List<InputSpreadsheet> result = facade.loadInputSpreadsheetsFromDirectory(Paths.get("testfiles", "entrada"));
		assertNotNull("Must generate list of input spreadsheets.", result);
	}

	@Test
	public void testGenerateOutputSpreadSheet_shouldGenerateValidOutput() throws IOException {
		List<InputSpreadsheet> inputlist = facade.loadInputSpreadsheetsFromDirectory(Paths.get("testfiles", "entrada"));
		OutputSpreadsheet result = facade.generateOutputSpreadSheet(inputlist);
		assertNotNull("Must generate output spreadsheets.", result);
		
	}

	@Test
	public void testGenerateOuputSpreadsheetFile_shouldGenerateValidOutput() throws IOException {
		List<InputSpreadsheet> inputlist = facade.loadInputSpreadsheetsFromDirectory(Paths.get("testfiles", "entrada"));
		OutputSpreadsheet outspreadsheet = facade.generateOutputSpreadSheet(inputlist);
		Path outputfile = Paths.get("testfiles", "saida", "testOutFromFacadeTest.xlsx");
		facade.generateOuputSpreadsheetFile(outputfile, outspreadsheet);
		assertTrue("Must generate output spreadsheet file.", Files.exists(outputfile));
	}

	@Test
	public void testGenerateOutputMessagesFile_shouldGenerateValidOutput() throws IOException {
		List<InputSpreadsheet> inputlist = facade.loadInputSpreadsheetsFromDirectory(Paths.get("testfiles", "entrada"));
		OutputSpreadsheet outspreadsheet = facade.generateOutputSpreadSheet(inputlist);
		Path outputfile = Paths.get("testfiles", "saida", "MensagensProcessamento.html");
		facade.generateOutputMessagesFile(outputfile, outspreadsheet);
		assertTrue("Must generate messages html file.", Files.exists(outputfile));
	}

}
