package com.tiagoamp.sjc.model.input;

import static com.tiagoamp.sjc.model.input.InputLayoutConstants.*;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InSheetTest {

	private InSheet sheet;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private XSSFSheet getExcelSheetForTest(SjcGeneralCode code) {
		XSSFSheet xssfsheet = null;
		Path inputFile = Paths.get("testfiles", "entrada", "template_input.xlsx");
		try ( FileInputStream fis = new FileInputStream(inputFile.toFile());
			  XSSFWorkbook xssworkbook = new XSSFWorkbook(fis); ) 
			{
			xssfsheet = xssworkbook.getSheet(code.getDescription().toUpperCase());			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xssfsheet;
	}
	
	
	@Test
	public void testLoadCellValueFrom_Lotacao_OperacionalSheet_shouldReturnCellValue() {
		SjcGeneralCode tipo = SjcGeneralCode.OPERACIONAL;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		String result = sheet.loadCellValueFrom(xssfsheet, new CellAddress(CELL_ADDRESS_LOTACAO_OPERACIONAL));
		assertEquals("'Operacional' sheet must return 'lotacao' from cell E3", "Nome da Unidade Célula E3", result);
	}
	
	@Test
	public void testLoadCellValueFrom_Lotacao_AdministrativoSheet_shouldReturnCellValue() {
		SjcGeneralCode tipo = SjcGeneralCode.ADMINISTRATIVO;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		String result = sheet.loadCellValueFrom(xssfsheet, new CellAddress(CELL_ADDRESS_LOTACAO_ADMISTRATIVO));
		assertEquals("'Administrativo' sheet must return 'lotacao' from cell B3", "Nome da Unidade Célula B3", result);
	}
	
	@Test
	public void testLoadCellValueFrom_Mes_OperacionalSheet_shouldReturnCelValue() {
		SjcGeneralCode tipo = SjcGeneralCode.OPERACIONAL;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		String result = sheet.loadCellValueFrom(xssfsheet, new CellAddress(CELL_ADDRESS_MES_OPERACIONAL));
		assertEquals("'Operacional' sheet must return 'mes' from cell I3", "NOME_DO_MÊS", result);
	}
	
	@Test
	public void testLoadCellValueFrom_Mes_AdministrativoSheet_shouldReturnCellValue() {
		SjcGeneralCode tipo = SjcGeneralCode.ADMINISTRATIVO;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		String result = sheet.loadCellValueFrom(xssfsheet, new CellAddress(CELL_ADDRESS_MES_ADMISTRATIVO));
		assertEquals("'Operacional' sheet must return 'mes' from cell G3", "NOME_DO_MÊS", result);
	}
	
	@Test
	public void testLoadCellValueFrom_Ano_OperacionalSheet_shouldReturnCellValue() {
		SjcGeneralCode tipo = SjcGeneralCode.OPERACIONAL;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		String result = sheet.loadCellValueFrom(xssfsheet, new CellAddress(CELL_ADDRESS_ANO_OPERACIONAL));
		assertEquals("'Operacional' sheet must return 'ano' from cell K3", "20XX", result);
	}
	
	@Test
	public void testLoadCellValueFrom_Mes_AdministrativoSheet_shouldReturnCellB3value() {
		SjcGeneralCode tipo = SjcGeneralCode.ADMINISTRATIVO;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		String result = sheet.loadCellValueFrom(xssfsheet, new CellAddress(CELL_ADDRESS_ANO_ADMISTRATIVO));
		assertEquals("'Operacional' sheet must return 'ano' from cell J3", "20XX", result);
	}

	@Test
	public void testLoadDataFrom_OperacionalSheet_shouldReturnData() {
		SjcGeneralCode tipo = SjcGeneralCode.OPERACIONAL;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		sheet.loadDataFrom(xssfsheet);
		int invalidRowNum = 21;
		assertEquals("'Operacional' sheet must have 17 rows", 17, sheet.getInputrows().size());
		assertTrue("Alert message (invalid row) should be generated.", sheet.getMessages().size() > 0);
		assertTrue("Alert message about invalid row number '" + invalidRowNum + "' should be generated.", 
				sheet.getMessages().get(0).getText().contains(String.valueOf(invalidRowNum)));
	}
	
	@Test
	public void testLoadDataFrom_AdministrativoSheet_shouldReturnData() {
		SjcGeneralCode tipo = SjcGeneralCode.ADMINISTRATIVO;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		sheet.loadDataFrom(xssfsheet);
		assertEquals("'Administrativo' sheet must have 9 rows", 9, sheet.getInputrows().size());
	}
	
}
