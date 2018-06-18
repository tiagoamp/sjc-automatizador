package com.tiagoamp.sjc.model.output;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.input.InputExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;

public class OutputExcelSpreadsheetTest {
	
	private OutputExcelSpreadsheet excelSpreadsheet;

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
			inputSpreadsheet = inputExcelSpreadsheet.loadFromFile();
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
	}
	
}
