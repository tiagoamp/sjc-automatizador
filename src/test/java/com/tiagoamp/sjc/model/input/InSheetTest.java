package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

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
	public void testLoadDataFrom_OperacionalSheet_shouldReturnData() {
		SjcGeneralCode tipo = SjcGeneralCode.OPERACIONAL;
		XSSFSheet xssfsheet = this.getExcelSheetForTest(tipo);
		sheet = new InSheet(tipo);
		sheet.loadDataFrom(xssfsheet);
		int invalidRowNum = 21;
		assertEquals("'Operacional' sheet must have 17 rows", 17, sheet.getRows().size());
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
		assertEquals("'Administrativo' sheet must have 9 rows", 9, sheet.getRows().size());
	}
	
}
