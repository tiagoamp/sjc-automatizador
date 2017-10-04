package com.tiagoamp.sjc.model.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.SjcGeneralCode;

public class OutputSpreadsheetTest {

	private OutputSpreadsheet outputSpreadsheet;
	
	@Before
	public void setUp() throws Exception {
		outputSpreadsheet = new OutputSpreadsheet();
	}

	@After
	public void tearDown() throws Exception {
		outputSpreadsheet = null;
	}

	private List<InputSpreadsheet> getInputSpreadsheetForTests() {
		InputSpreadsheet inputSpreadSheet = new InputSpreadsheet();
		inputSpreadSheet.setLotacao("Unidade de Testes");
		try {
			inputSpreadSheet.loadFromFile(Paths.get("testfiles", "entrada", "template_input.xlsx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<InputSpreadsheet> list = new ArrayList<>();
		list.add(inputSpreadSheet);
		return list;
	}
	
	@Test
	public void testLoadDataFromInputSpreadSheets_shouldGenerateOutputSpreadSheet() {
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		outputSpreadsheet.loadDataFromInputSpreadSheets(inputlist);
		assertEquals("Must generate 5 output sheets", 5, outputSpreadsheet.getSheets().size());
		
		for (OutSheet sheet : outputSpreadsheet.getSheets()) {
			if (sheet.getCode().getGenericCode() == SjcGeneralCode.OPERACIONAL) {
				assertEquals("'Operacional' sheet should generate 17 rows", 17, sheet.getOutputrows().size());
			} else if (sheet.getCode().getGenericCode() == SjcGeneralCode.ADMINISTRATIVO) {
				assertEquals("'Operacional' sheet should generate 9 rows", 9, sheet.getOutputrows().size());
			}
		}
	}
	
	@Test
	public void testGenerateOuputSpreadSheet_shouldGenerateFileInSystem() throws IOException {
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		outputSpreadsheet.loadDataFromInputSpreadSheets(inputlist);
		Path templateFile = Paths.get("resources","template_output.xlsx");
		Path outputTestFile = Paths.get("testfiles", "saida", "testOutFromSpreadSheetTest.xlsx");
		
		outputSpreadsheet.generateOuputSpreadsheetFile(outputTestFile, templateFile);
		assertTrue("File should be created in filesystem.", Files.exists(outputTestFile));
		assertTrue("Output file must be bigger than original template file.", Files.size(outputTestFile) > Files.size(templateFile) );
	}
	
	@Test
	public void testGenerateOutputMessageFile_shouldGenerateFileInSystem() throws FileNotFoundException, DocumentException {
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		outputSpreadsheet.loadDataFromInputSpreadSheets(inputlist);
		Path outputTestFile = Paths.get("testfiles", "saida", "testOutMessageFromSpreadSheetTest.pdf");
		
		outputSpreadsheet.generateOutputMessageFile(outputTestFile);
		assertTrue("File should be created in filesystem.", Files.exists(outputTestFile));
	}
		
}
