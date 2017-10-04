package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InputSpreadsheetTest {
	
	private InputSpreadsheet inputSpreadsheet;

	@Before
	public void setUp() throws Exception {
		inputSpreadsheet = new InputSpreadsheet();
	}

	@After
	public void tearDown() throws Exception {
		inputSpreadsheet = null;
	}

	
	@Test
	public void testLoadFromFile_directory_shouldReturnErrorProcessingMessage() throws IOException {
		Path directoryFile = Paths.get("testfiles","entrada","dummyDirectory");		
		inputSpreadsheet.loadFromFile(directoryFile);
		assertTrue("Processing messages must not be empty", !inputSpreadsheet.getMessages().isEmpty());
		assertEquals("Must have directory error message", MessageType.ERROR, inputSpreadsheet.getMessages().get(0).getType());
		assertTrue("Must have directory error message", inputSpreadsheet.getMessages().get(0).getText().contains("diretório"));
	}
	
	@Test
	public void testLoadFromFile_notXLSXfile_shouldReturnErrorProcessingMessage() throws IOException {
		Path dummyFile = Paths.get("testfiles","entrada","dummyFile.dmp");
		inputSpreadsheet.loadFromFile(dummyFile);
		assertTrue("Processing messages must not be empty", !inputSpreadsheet.getMessages().isEmpty());
		assertEquals("Must have error message", MessageType.ERROR, inputSpreadsheet.getMessages().get(0).getType());
		assertTrue("Must have xlsx error message", inputSpreadsheet.getMessages().get(0).getText().contains("xlsx"));
	}
	
	@Test
	public void testLoadFromFile_emptySheet_shouldReturnErrorProcessingMessage() throws IOException {
		Path inputFile = Paths.get("testfiles","entrada","blank_sheet.xlsx");
		inputSpreadsheet.loadFromFile(inputFile);
		assertTrue("Processing messages must not be empty", !inputSpreadsheet.getMessages().isEmpty());
		assertEquals("Must have 3 errors message", 3, inputSpreadsheet.getMessages().size());
		assertTrue("Must have sheet not found error message (1st)", inputSpreadsheet.getMessages().get(0).getText().contains("não encontrada"));
		assertTrue("Must have sheet not found error message (2nd)", inputSpreadsheet.getMessages().get(1).getText().contains("não encontrada"));
		assertTrue("Must have 'lotacao' not found error message (3rd)", inputSpreadsheet.getMessages().get(2).getText().contains("Não foi identificado"));
		assertEquals("'Lotacao' must be setted to expected", "!NOME DA UNIDADE NÃO IDENTIFICADO NA PLANILHA!", inputSpreadsheet.getLotacao());
		
	}
	
	@Test
	public void testGetInpuSheetFromGenericCode_shouldReturnOperacionalSheet() {
		InSheet sheetAdm = new InSheet(SjcGeneralCode.ADMINISTRATIVO);
		InSheet sheetOp = new InSheet(SjcGeneralCode.OPERACIONAL);
		InSheet result = null;
		inputSpreadsheet.getSheets().add(sheetAdm);
		inputSpreadsheet.getSheets().add(sheetOp);
		
		result = inputSpreadsheet.getInpuSheetFromGenericCode(SjcGeneralCode.OPERACIONAL);
		assertEquals("Should return 'OPERACIONAL' sheet.", SjcGeneralCode.OPERACIONAL, result.getCode());
	}
	
	@Test
	public void testGetInpuSheetFromGenericCode_shouldReturnAdministrativoSheet() {
		InSheet sheetAdm = new InSheet(SjcGeneralCode.ADMINISTRATIVO);
		InSheet sheetOp = new InSheet(SjcGeneralCode.OPERACIONAL);
		InSheet result = null;
		inputSpreadsheet.getSheets().add(sheetAdm);
		inputSpreadsheet.getSheets().add(sheetOp);		
		
		result = inputSpreadsheet.getInpuSheetFromGenericCode(SjcGeneralCode.ADMINISTRATIVO);
		assertEquals("Should return 'ADMINISTRATIVO' sheet.", SjcGeneralCode.ADMINISTRATIVO, result.getCode());
	}

}
