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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.service.PDFGenerator;

public class OutputFilesGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OutputFilesGenerator.class);

	public void generateOuputSpreadsheetFile(Path outputFile, OutputSpreadsheet spreadsheet) throws IOException  {		
		try ( XSSFWorkbook workbook = new XSSFWorkbook();
			  FileOutputStream fos = new FileOutputStream(outputFile.toFile()); ) 
			{
			for (SjcSpecificCode code : SjcSpecificCode.values()) {
				OutSheet sheet = spreadsheet.getSheets().get(code);
				if (sheet == null || sheet.getRows() == null || sheet.getRows().size() == 0) continue;
				
				LOGGER.info("Ordenando linhas da planilha de saída [" + code.getCode().toString() + "] ...");
				sheet.sortRows();				
				Map<Integer, Object[]> data = this.createOutputDataMap(sheet);								
				XSSFSheet xsheet = workbook.createSheet(String.valueOf(code.getCode()));
				
				LOGGER.info("Preenchendo linhas da planilha de saída [" + code.getCode().toString() + "] ...");
				boolean hasToMergeRows = false;
				fillNewOuputRowsInExcelSheet(xsheet, data, hasToMergeRows);				
								
				int numberOfColumns = 11;
				for (int i = 0; i < numberOfColumns; i++) {
					xsheet.autoSizeColumn(i); // column adjusting
				}
				
				if (code == SjcSpecificCode.OPERACIONAL_PLANTOESEXTRA) {
					OutSheet sheetGroupMatriculas = new OutSheet(SjcSpecificCode.OPERACIONAL_PLANTOESEXTRA);
					sheetGroupMatriculas.setRows(sheet.getRows());
					LOGGER.info("Agrupando linhas da planilha de saída [" + code.getCode() + " Agrupado" + "] ...");
					sheetGroupMatriculas.mergeRows();
					Map<Integer, Object[]> data2 = this.createOutputDataMap(sheetGroupMatriculas);									
					XSSFSheet xsheet2 = workbook.createSheet(String.valueOf(code.getCode() + "_agrupado"));
					LOGGER.info("Preenchendo linhas da planilha de saída [" + code.getCode()+ " Agrupado" +  "] ...");
					hasToMergeRows = true;
					fillNewOuputRowsInExcelSheet(xsheet2, data2, hasToMergeRows);				
									
					for (int i = 0; i < numberOfColumns; i++) {
						xsheet2.autoSizeColumn(i); // column adjusting
					}
				}
			}
			
			workbook.write(fos);
			LOGGER.info("Planilha de saída gravada!");
		}
	}
	
	@Deprecated
	public void generateOutputMessageFile(Path outputFile, OutputSpreadsheet spreadsheet) throws FileNotFoundException, DocumentException {
		PDFGenerator pdfGen = new PDFGenerator();
		pdfGen.generateMessagesPdfFile(spreadsheet.getMessages(), outputFile);
	}
	
	
	private Map<Integer, Object[]> createOutputDataMap(OutSheet sheet) {
		Map<Integer, Object[]> data = new HashMap<>();
		Integer counter = 0;	         
		for (OutRow outRow : sheet.getRows()) {
			data.put(counter, new Object[] {outRow.getLotacao(), outRow.getNome().toUpperCase(), outRow.getMatricula(), outRow.getQuantidade(), 
				outRow.getDtPlantoesExtras()[0], outRow.getDtPlantoesExtras()[1], outRow.getDtPlantoesExtras()[2], outRow.getDtPlantoesExtras()[3], outRow.getDtPlantoesExtras()[4], 
				outRow.getAfastamento(), outRow.getDtPlantoesWithinAfastamentos(),
				outRow.getMessage()});
			counter++;
    	}
		return data;
	}
	
	private void fillNewOuputRowsInExcelSheet(XSSFSheet xssfsheet, Map<Integer, Object[]> data, boolean hasToMergeRows) {
		Set<Integer> newRows = data.keySet(); // Set to Iterate and add rows into XLS file
		int rownum = xssfsheet.getLastRowNum(); // get the last row number to append new data   
		final int initPlantoesObjIndex = 4, afastamentoIndex = 9, dtPlantoesAfastIndex = 10, rowMessageIndex = 11;
		
		for (Integer key : newRows) {
			Row row = xssfsheet.createRow(rownum++); // Creating a new Row in existing XLSX sheet
			Object [] objArr = data.get(key);
			
			Object flagsPlantoesAfast = objArr[9];
			boolean hasPlantoesExtrasWithAfastamentos = flagsPlantoesAfast != null; 
			Boolean[] dtPlantoesWithinAfastamentos = null;
			
			if (hasPlantoesExtrasWithAfastamentos) dtPlantoesWithinAfastamentos = (Boolean[]) objArr[dtPlantoesAfastIndex];
			
			boolean isRowWithPlantaoExtra = objArr[initPlantoesObjIndex] != null;
			
			int cellnum = 0;
			for (int i=0; i < objArr.length; i++ ) {
				Object obj = objArr[i];				
				
				boolean isPlantaoDatesInterval = i >= initPlantoesObjIndex && i < initPlantoesObjIndex+5;
				if ( (isPlantaoDatesInterval && !isRowWithPlantaoExtra) || (i == dtPlantoesAfastIndex) || (!isRowWithPlantaoExtra && i == afastamentoIndex)) {
					continue; 
				}
				
				Cell cell = row.createCell(cellnum++);
				
				if (obj != null && isPlantaoDatesInterval && isRowWithPlantaoExtra && hasPlantoesExtrasWithAfastamentos) {
					Boolean hasDatesConflicts = dtPlantoesWithinAfastamentos[i - initPlantoesObjIndex];
					XSSFCellStyle cellStyle = null;
					if (hasDatesConflicts == null) cellStyle = getCellStyleFor(xssfsheet, "not-evaluated");
					else if (hasDatesConflicts) cellStyle = getCellStyleFor(xssfsheet, "conflict");
					
					if (cellStyle != null) cell.setCellStyle(cellStyle);
				}
				
				if (i == afastamentoIndex) { // afastamento
					XSSFCellStyle italicCellStyle = getItalicCellStyle(xssfsheet);
					cell.setCellStyle(italicCellStyle);
				}
				
				/*
				 * if (i == rowMessageIndex) { // msgs for repeated matricula if (isRepeated &&
				 * !hasToMergeRows) { //String m = (String) objArr[duplicatedMessageIndex];
				 * String msg = "** Matrícula repete nesta planilha para outra lotação"; obj =
				 * msg; } else { obj = null; } }
				 */
								
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
		else if (type.equals("not-evaluated")) font.setColor(HSSFColor.HSSFColorPredefined.LIGHT_ORANGE.getIndex());
		
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
