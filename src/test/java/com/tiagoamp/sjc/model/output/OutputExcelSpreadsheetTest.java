package com.tiagoamp.sjc.model.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.model.input.AfastamentosExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.HistoricoAfastamentos;
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
		OutputSpreadsheet outputSpreadsheet = excelSpreadsheet.loadDataFromInputSpreadsheets(inputlist, null);
		
		assertEquals("Must generate 5 output sheets", 5, outputSpreadsheet.getSheets().size());
		
		outputSpreadsheet.getSheets().keySet()
			.forEach(key -> {
				OutSheet sheet = outputSpreadsheet.getSheets().get(key);
				if (sheet.getCode().getGenericCode() == SjcGeneralCode.OPERACIONAL) {
					assertEquals("'Operacional' sheet should generate 17 rows", 17, sheet.getRows().size());
				} else if (sheet.getCode().getGenericCode() == SjcGeneralCode.ADMINISTRATIVO) {
					assertEquals("'Operacional' sheet should generate 9 rows", 9, sheet.getRows().size());
				}
			});		
	}
	
	@Test
	public void testLoadDataFromInputSpreadSheets_withAfastamentosSpreadsheet_shouldGenerateOutputSpreadSheet() throws IOException {
		// given
		List<InputSpreadsheet> inputlist = getInputSpreadsheetForTests();
		AfastamentosExcelSpreadsheet afastSpreadsheet = new AfastamentosExcelSpreadsheet(Paths.get("testfiles", "entrada", "template_HistoricoAfastamento.xlsx"));
		HistoricoAfastamentos afastamentos = afastSpreadsheet.loadFromFile();
		//when
		OutputSpreadsheet outputSpreadsheet = excelSpreadsheet.loadDataFromInputSpreadsheets(inputlist, afastamentos);
		//then
		String[] matriculasWithAfastamentos = {"0691513203", "0691513204"};
		List<OutRow> rowsWithAfastamentos = outputSpreadsheet.getSheets().get(SjcSpecificCode.OPERACIONAL_PLANTOESEXTRA).getRows().stream()
			.filter(row -> row.getMatricula().equals(matriculasWithAfastamentos[0]) || row.getMatricula().equals(matriculasWithAfastamentos[1]))
			.collect(Collectors.toList());
		assertEquals("Should generate " + matriculasWithAfastamentos.length + " row with afastamentos",  matriculasWithAfastamentos.length, rowsWithAfastamentos.size());
		boolean hasAfastamentosFilled = rowsWithAfastamentos.get(0).getAfastamento() != null && rowsWithAfastamentos.get(1).getAfastamento() != null; 
		assertTrue("Must have 'afastamento' settled in rows", hasAfastamentosFilled);
		boolean hasPlantaoWithAfastamento01 = Arrays.stream(rowsWithAfastamentos.get(0).getDtPlantoesWithinAfastamentos()).filter(value -> value == true).findAny().isPresent();
		boolean hasPlantaoWithAfastamento02 = Arrays.stream(rowsWithAfastamentos.get(1).getDtPlantoesWithinAfastamentos()).filter(value -> value == true).findAny().isPresent();
		boolean hasPlantoesBeenEvaluatedWithinAfastamentos = hasPlantaoWithAfastamento01 || hasPlantaoWithAfastamento02;
		assertTrue("Must have 'plantoes' been evaluated with 'afastamento'", hasPlantoesBeenEvaluatedWithinAfastamentos);
		
	}
	
}
