package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AfastamentosExcelSpreadsheetTest {

	private AfastamentosExcelSpreadsheet excelSpreadsheet;
	
	
	@Before
	public void setUp() throws Exception {		
	}

	@After
	public void tearDown() throws Exception {
		excelSpreadsheet = null;
	}
	
	
	@Test
	public void testLoadFromFile() throws IOException {
		// given
		Path directoryFile = Paths.get("testfiles","entrada","template_HistoricoAfastamento.xlsx");
		excelSpreadsheet = new AfastamentosExcelSpreadsheet(directoryFile);
		// when
		HistoricoAfastamentos historicoAfastamentos = excelSpreadsheet.loadFromFile();
		// then 
		int amountOfRowsInFile = 20;
		assertEquals("Result should have " + amountOfRowsInFile + ".", amountOfRowsInFile, historicoAfastamentos.getSheet().getRows().size());		
	}

}
