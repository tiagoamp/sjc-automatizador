package com.tiagoamp.sjc.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.input.v3.ConvertedSheet;
import com.tiagoamp.sjc.model.input.v3.ConvertedSpreadsheet;

public class ExcelFileDaoTest {

	private Path pdfFile = Paths.get("testfiles","entrada","new","Modelo.PDF");
	private Path excelFile = Paths.get("testfiles","entrada","new","ModeloExcel.xlsx");
	private Path resultFile = pdfFile.getParent().resolve("ModeloGerado.xlsx");
	private ExcelFileDao dao;
	
	@Before
	public void setup() throws IOException {
		
		Files.deleteIfExists(resultFile);
		dao = new ExcelFileDao();
	}
	

	@Test
	public void testCreateConvertedSpreadsheet() throws FileNotFoundException, IOException {		
		// given
		String convertedFileName = "ModeloGerado.xlsx";
		ConvertedSpreadsheet spreadsheet = dao.loadFrom(excelFile);
		spreadsheet.setOriginalFile(pdfFile);
		// when
		dao.createConvertedSpreadsheet(spreadsheet, convertedFileName);
		assertTrue(Files.exists(resultFile));
	}

	
	@Test
	public void testLoadFrom() throws FileNotFoundException, IOException {
		ConvertedSpreadsheet spreadsheet = dao.loadFrom(excelFile);
		assertNotNull(spreadsheet.getConvertedFile());
		assertNotNull(spreadsheet.getHeader());
		assertNotNull(spreadsheet.getHeader().getYearRefAsStr());
		assertNotNull(spreadsheet.getHeader().getMonthRefAsStr());
		assertNotNull(spreadsheet.getHeader().getNomeUnidadePrisional());		
		for (SjcGeneralCode code : SjcGeneralCode.values()) {
			ConvertedSheet sheet = spreadsheet.getConvertedSheets().get(code);
			assertNotNull(sheet);
			assertTrue(sheet.getRows().size() == (sheet.getCode() == SjcGeneralCode.OPERACIONAL ? 8 : 11));
		}
	}

}
