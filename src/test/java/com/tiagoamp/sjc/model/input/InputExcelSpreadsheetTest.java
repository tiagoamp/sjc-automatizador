package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.MessageType;

public class InputExcelSpreadsheetTest {

	private InputExcelSpreadsheet inputExcelSpreadsheet;

	@Before
	public void setUp() throws Exception {		
	}

	@After
	public void tearDown() throws Exception {
		inputExcelSpreadsheet = null;
	}

	@Test
	public void testLoadFromFile_directory_shouldReturnErrorProcessingMessage() throws IOException {
		Path directoryFile = Paths.get("testfiles","entrada","dummyDirectory");
		inputExcelSpreadsheet = new InputExcelSpreadsheet(directoryFile);
		InputSpreadsheet spreadsheet = inputExcelSpreadsheet.loadFromFile();
		assertTrue("Processing messages must not be empty", !spreadsheet.getMessages().isEmpty());
		assertEquals("Must error message type", MessageType.ERROR, spreadsheet.getMessages().get(0).getType());
		assertTrue("Must have directory error message", spreadsheet.getMessages().get(0).getText().contains("diretório"));
	}
	
	@Test
	public void testLoadFromFile_notXLSXfile_shouldReturnErrorProcessingMessage() throws IOException {
		Path dummyFile = Paths.get("testfiles","entrada","dummyFile.dmp");
		inputExcelSpreadsheet = new InputExcelSpreadsheet(dummyFile);
		InputSpreadsheet spreadsheet = inputExcelSpreadsheet.loadFromFile();
		assertTrue("Processing messages must not be empty", !spreadsheet.getMessages().isEmpty());
		assertEquals("Must have error message", MessageType.ERROR, spreadsheet.getMessages().get(0).getType());
		assertTrue("Must have xlsx error message", spreadsheet.getMessages().get(0).getText().contains("xlsx"));
	}
	
	@Test
	public void testLoadFromFile_emptySheet_shouldReturnErrorProcessingMessage() throws IOException {
		Path inputFile = Paths.get("testfiles","entrada","blank_sheet.xlsx");
		inputExcelSpreadsheet = new InputExcelSpreadsheet(inputFile);
		InputSpreadsheet spreadsheet = inputExcelSpreadsheet.loadFromFile();
		assertTrue("Processing messages must not be empty", !spreadsheet.getMessages().isEmpty());
		assertEquals("Must have 3 errors message", 3, spreadsheet.getMessages().size());
		assertTrue("Must have sheet not found error message (1st)", spreadsheet.getMessages().get(0).getText().contains("não encontrada"));
		assertTrue("Must have sheet not found error message (2nd)", spreadsheet.getMessages().get(1).getText().contains("não encontrada"));
		assertTrue("Must have 'lotacao' not found error message (3rd)", spreadsheet.getMessages().get(2).getText().contains("Não foi identificado"));
		assertEquals("'Lotacao' must be setted to expected", "!NOME DA UNIDADE NÃO IDENTIFICADO NA PLANILHA!", spreadsheet.getLotacao());
		
	}

}
