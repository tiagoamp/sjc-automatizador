package com.tiagoamp.sjc.model.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.service.PDFGenerator;

public class OutputFilesGenerator {

	public void generateOuputSpreadsheetFile(Path outputFile, OutputSpreadsheet spreadsheet) throws IOException  {		
		try ( XSSFWorkbook workbook = new XSSFWorkbook();
			  FileOutputStream fos = new FileOutputStream(outputFile.toFile()); ) 
			{
			for (SjcSpecificCode code : SjcSpecificCode.values()) {
				OutSheet sheet = spreadsheet.getSheets().get(code);
				if (sheet == null || sheet.getOutputrows() == null || sheet.getOutputrows().size() == 0) continue;
				
				sheet.sortRows();
				
				Map<Integer, Object[]> data = this.createOutputDataMap(sheet);
								
				XSSFSheet xsheet = workbook.createSheet(String.valueOf(code.getCode()));
				
				fillNewOuputRowsInExcelSheet(xsheet, data);				
								
				int numberOfColumns = 10;
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
				outRow.getDtPlantoesExtras()[3], outRow.getDtPlantoesExtras()[4], outRow.getAfastamento(), outRow.getDtPlantoesWithinAfastamentos()});
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
			
			Object flagsPlantoesAfast = objArr[9];
			boolean hasPlantoesExtrasWithAfastamentos = flagsPlantoesAfast != null; 
			Boolean[] dtPlantoesWithinAfastamentos = null;
			if (hasPlantoesExtrasWithAfastamentos) dtPlantoesWithinAfastamentos = (Boolean[]) objArr[10];
			
			int initPlantoesObjIndex = 4;
			boolean isRowWithPlantaoExtra = objArr[initPlantoesObjIndex] != null;
			
			int cellnum = 0;
			for (int i=0; i < objArr.length; i++ ) {
				Object obj = objArr[i];				
				Cell cell = row.createCell(cellnum++);
				
				boolean isPlantaoDatesInterval = i >= initPlantoesObjIndex && i < initPlantoesObjIndex+5;  
				if (obj != null && isPlantaoDatesInterval && isRowWithPlantaoExtra && hasPlantoesExtrasWithAfastamentos) {
					Boolean hasDatesConflicts = dtPlantoesWithinAfastamentos[i - initPlantoesObjIndex];
					XSSFCellStyle cellStyle = null;
					if (hasDatesConflicts == null) cellStyle = getCellStyleFor(xssfsheet, "not-evaluated");
					else if (hasDatesConflicts) cellStyle = getCellStyleFor(xssfsheet, "conflict");
					
					if (cellStyle != null) cell.setCellStyle(cellStyle);
				}
				
				int afastamentoIndex = 9;
				if (i == afastamentoIndex) { // afastamento
					XSSFCellStyle italicCellStyle = getItalicCellStyle(xssfsheet);
					cell.setCellStyle(italicCellStyle);
				}
								
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
	
	private XSSFCellStyle getCellStyleFor(XSSFSheet xssfsheet, String type) {
		XSSFFont font = xssfsheet.getWorkbook().createFont();
		
		if (type.equals("conflict")) font.setColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
		else if (type.equals("not-evaluated")) font.setColor(HSSFColor.HSSFColorPredefined.DARK_YELLOW.getIndex());
		
		XSSFCellStyle style = xssfsheet.getWorkbook().createCellStyle();
		style.setFont(font);
		
		return style;
	}
	
	private XSSFCellStyle getItalicCellStyle(XSSFSheet xssfsheet) {
		XSSFFont font = xssfsheet.getWorkbook().createFont();
		font.setItalic(true);
		XSSFCellStyle style = xssfsheet.getWorkbook().createCellStyle();
		style.setFont(font);		
		return style;
	}
	
}
