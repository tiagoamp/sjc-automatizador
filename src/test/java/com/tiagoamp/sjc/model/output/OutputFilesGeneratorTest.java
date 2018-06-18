package com.tiagoamp.sjc.model.output;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.input.InputExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;

public class OutputFilesGeneratorTest {

	private OutputFilesGenerator filesGenerator;
	
	@BeforeClass
	public static void init() throws Exception {
		Path outDir =  Paths.get("testfiles", "saida");
		if (Files.notExists(outDir)) Files.createDirectories(outDir);
	}
	
		@Before
	public void setUp() throws Exception {
		filesGenerator = new OutputFilesGenerator();
	}

	@After
	public void tearDown() throws Exception {
		filesGenerator = null;
	}

	
	private List<InputSpreadsheet> getInputSpreadsheetForTests() {
		InputExcelSpreadsheet inputExcelSpreadsheet = new InputExcelSpreadsheet(Paths.get("testfiles", "entrada", "template_input.xlsx"));
		InputSpreadsheet inputSpreadsheet = null;
		try {
			inputSpreadsheet = inputExcelSpreadsheet.loadFromFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<InputSpreadsheet> list = new ArrayList<>();
		list.add(inputSpreadsheet);
		return list;
	}

	
	@Test
	public void testGenerateOuputSpreadSheet_shouldGenerateFileInSystem() throws IOException {
		// given
		Path outputTestFile = Paths.get("testfiles", "saida", "testOutFromSpreadSheetTest.xlsx");
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		OutputExcelSpreadsheet excelSpreadsheet = new OutputExcelSpreadsheet();
		OutputSpreadsheet spreadsheet = excelSpreadsheet.loadDataFromInputSpreadsheets(inputlist);				
		// when
		filesGenerator.generateOuputSpreadsheetFile(outputTestFile, spreadsheet);		
		// then
		assertTrue("File should be created in filesystem.", Files.exists(outputTestFile));
	}
	
	@Test
	public void testGenerateOutputMessageFile_shouldGenerateFileInSystem() throws FileNotFoundException, DocumentException {
		// given
		Path outputTestFile = Paths.get("testfiles", "saida", "testOutMessageFromSpreadSheetTest.pdf");
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		OutputExcelSpreadsheet excelSpreadsheet = new OutputExcelSpreadsheet();
		OutputSpreadsheet spreadsheet = excelSpreadsheet.loadDataFromInputSpreadsheets(inputlist);		
		// when
		filesGenerator.generateOutputMessageFile(outputTestFile, spreadsheet);		
		// then
		assertTrue("File should be created in filesystem.", Files.exists(outputTestFile));
	}

}
