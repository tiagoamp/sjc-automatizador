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
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.input.InputExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;

public class OutputExcelSpreadsheetTest {
	
	private OutputExcelSpreadsheet excelSpreadsheet;

	@BeforeClass
	public static void init() throws Exception {
		Path outDir =  Paths.get("testfiles", "saida");
		if (Files.notExists(outDir)) Files.createDirectories(outDir);
	}
	
	@Before
	public void setUp() throws Exception {
		excelSpreadsheet = new OutputExcelSpreadsheet();
	}

	@After
	public void tearDown() throws Exception {
		excelSpreadsheet = null;
	}

	
	private List<InputSpreadsheet> getInputSpreadsheetForTests() {
		InputExcelSpreadsheet inputExcelSpreadsheet = new InputExcelSpreadsheet(Paths.get("testfiles", "entrada", "template_input.xlsx"));
		InputSpreadsheet inputSpreadsheet = null;
		try {
			inputSpreadsheet = inputExcelSpreadsheet.toInputSpreadsheet();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<InputSpreadsheet> list = new ArrayList<>();
		list.add(inputSpreadsheet);
		return list;
	}
	
	@Test
	public void testLoadDataFromInputSpreadSheets_shouldGenerateOutputSpreadSheet() {
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		OutputSpreadsheet outputSpreadsheet = excelSpreadsheet.loadDataFromInputSpreadsheets(inputlist);
		
		assertEquals("Must generate 5 output sheets", 5, outputSpreadsheet.getSheets().size());
		
		outputSpreadsheet.getSheets().keySet()
			.forEach(key -> {
				OutSheet sheet = outputSpreadsheet.getSheets().get(key);
				if (sheet.getCode().getGenericCode() == SjcGeneralCode.OPERACIONAL) {
					assertEquals("'Operacional' sheet should generate 17 rows", 17, sheet.getOutputrows().size());
				} else if (sheet.getCode().getGenericCode() == SjcGeneralCode.ADMINISTRATIVO) {
					assertEquals("'Operacional' sheet should generate 9 rows", 9, sheet.getOutputrows().size());
				}
			});
		
		/*for (OutSheet sheet : outputSpreadsheet.getSheets()) {
			if (sheet.getCode().getGenericCode() == SjcGeneralCode.OPERACIONAL) {
				assertEquals("'Operacional' sheet should generate 17 rows", 17, sheet.getOutputrows().size());
			} else if (sheet.getCode().getGenericCode() == SjcGeneralCode.ADMINISTRATIVO) {
				assertEquals("'Operacional' sheet should generate 9 rows", 9, sheet.getOutputrows().size());
			}
		}*/
	}
	
	@Test
	public void testGenerateOuputSpreadSheet_shouldGenerateFileInSystem() throws IOException {
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		excelSpreadsheet.loadDataFromInputSpreadsheets(inputlist);
		//Path templateFile = Paths.get("resources","template_output.xlsx");
		Path outputTestFile = Paths.get("testfiles", "saida", "testOutFromSpreadSheetTest.xlsx");
		
		excelSpreadsheet.generateOuputSpreadsheetFile(outputTestFile);		
		//outputSpreadsheet.generateOuputSpreadsheetFile(outputTestFile, templateFile);
		assertTrue("File should be created in filesystem.", Files.exists(outputTestFile));
		//assertTrue("Output file must be bigger than original template file.", Files.size(outputTestFile) > Files.size(templateFile) );
	}
	
	@Test
	public void testGenerateOutputMessageFile_shouldGenerateFileInSystem() throws FileNotFoundException, DocumentException {
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		excelSpreadsheet.loadDataFromInputSpreadsheets(inputlist);
		Path outputTestFile = Paths.get("testfiles", "saida", "testOutMessageFromSpreadSheetTest.pdf");
		
		excelSpreadsheet.generateOutputMessageFile(outputTestFile);
		assertTrue("File should be created in filesystem.", Files.exists(outputTestFile));
	}

}
