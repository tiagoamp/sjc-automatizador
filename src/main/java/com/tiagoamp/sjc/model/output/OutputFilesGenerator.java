package com.tiagoamp.sjc.model.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.service.PDFGenerator;

public class OutputFilesGenerator {

	public void generateOuputSpreadsheetFile(Path outputFile, OutputSpreadsheet spreadsheet) throws IOException  {		
		try (XSSFWorkbook workbook = new XSSFWorkbook();
			 FileOutputStream fos = new FileOutputStream(outputFile.toFile());  
			) 
			{
			for (SjcSpecificCode code : SjcSpecificCode.values()) {
				OutSheet sheet = spreadsheet.getSheets().get(code);
				if (sheet == null || sheet.getOutputrows() == null || sheet.getOutputrows().size() == 0) continue; 
				sheet.getOutputrows().sort((r1,r2) -> r1.getLotacao().compareTo(r2.getLotacao()));
						    		
				Map<Integer, Object[]> data = this.createOutputDataMap(sheet);
				
				XSSFSheet xsheet = workbook.createSheet(String.valueOf(code.getCode()));
				
				fillNewOuputRowsInExcelSheet(xsheet, data);
				
				int numberOfColumns = 9;
				for (int i = 0; i < numberOfColumns; i++) {
					xsheet.autoSizeColumn(i); // column adjusting
				}
			}
			workbook.write(fos);
		}
	}
	
	public void generateOutputMessageFile(Path outputFile, OutputSpreadsheet spreadsheet) throws FileNotFoundException, DocumentException {
		PDFGenerator pdfGen = new PDFGenerator();
		pdfGen.generateMessagesPdfFile(spreadsheet.getMessages(), outputFile);
	}
	
	private Map<Integer, Object[]> createOutputDataMap(OutSheet sheet) {
		Map<Integer, Object[]> data = new HashMap<>();
		Integer counter = 0;	         
		for (OutRow outRow : sheet.getOutputrows()) {
			data.put(counter, new Object[] {outRow.getLotacao(), outRow.getNome(), outRow.getMatricula(), outRow.getQuantidade(), 
					outRow.getDtPlantoesExtras()[0], outRow.getDtPlantoesExtras()[1], outRow.getDtPlantoesExtras()[2], 
					outRow.getDtPlantoesExtras()[3], outRow.getDtPlantoesExtras()[4]});
			counter++;
    	}
		return data;
	}
	
	private void fillNewOuputRowsInExcelSheet(XSSFSheet xssfsheet, Map<Integer, Object[]> data) {
		Set<Integer> newRows = data.keySet(); // Set to Iterate and add rows into XLS file
		int rownum = xssfsheet.getLastRowNum(); // get the last row number to append new data        
		for (Integer key : newRows) {
			Row row = xssfsheet.createRow(rownum++); // Creating a new Row in existing XLSX sheet
			Object [] objArr = data.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if (obj instanceof String) {
					cell.setCellValue((String) obj);
                } else if (obj instanceof Boolean) {
                	cell.setCellValue((Boolean) obj);
                } else if (obj instanceof Date) {
                	cell.setCellValue((Date) obj);
                } else if (obj instanceof Double) {
                	cell.setCellValue((Double) obj);
                } else if (obj instanceof Integer) {
                	cell.setCellValue((Integer) obj);
                }	                    
			}      
		}				
	}
	
}
