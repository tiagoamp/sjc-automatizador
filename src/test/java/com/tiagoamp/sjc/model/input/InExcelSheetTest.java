package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InExcelSheetTest {
	
	private InExcelSheet excelsheet;

	@Before
	public void setUp() throws Exception {		
	}

	@After
	public void tearDown() throws Exception {
		excelsheet = null;
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
	public void testLoadDataFrom_OperacionalSheet_shouldReturnData() {
		// given
		SjcGeneralCode code = SjcGeneralCode.OPERACIONAL;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(code);
		
		// when
		excelsheet = new InExcelSheet(code, YearMonth.now());
		InSheet sheet = excelsheet.loadDataFrom(xssfsheet);
		
		// then
		final int invalidRowNum = 21;
		assertEquals("'Operacional' sheet must have 17 rows", 17, sheet.getRows().size());
		assertTrue("Alert message (invalid row) should be generated.", sheet.getMessages().size() > 0);
		assertTrue("Alert message about invalid row number '" + invalidRowNum + "' should be generated.", 
				sheet.getMessages().get(0).getText().contains(String.valueOf(invalidRowNum)));
	}
	
	@Test
	public void testLoadDataFrom_AdministrativoSheet_shouldReturnData() {
		// given
		SjcGeneralCode code = SjcGeneralCode.ADMINISTRATIVO;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(code);		
		// when
		excelsheet = new InExcelSheet(code, YearMonth.now());
		InSheet sheet = excelsheet.loadDataFrom(xssfsheet);		
		// then
		assertEquals("'Administrativo' sheet must have 9 rows", 9, sheet.getRows().size());
	}

}
