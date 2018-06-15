package com.tiagoamp.sjc.model.output;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.SjcItemType;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.model.input.InRow;
import com.tiagoamp.sjc.model.input.InSheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.service.PDFGenerator;

public class OutputExcelSpreadsheet {
	
	private OutputSpreadsheet spreadsheet;
	
	
	public OutputExcelSpreadsheet() {
		spreadsheet = new OutputSpreadsheet();
	}
	

	public OutputSpreadsheet loadDataFromInputSpreadsheets(List<InputSpreadsheet> inputSoreadsheets) {		
		inputSoreadsheets.forEach(inputSpreadsheet -> {
			loadDataFromInputSpreadsheet(inputSpreadsheet);			
			spreadsheet.getMessages().put(inputSpreadsheet.getFileName(), inputSpreadsheet.getMessages());
		}); 
		return spreadsheet;
	}
	
	public void generateOuputSpreadsheetFile(Path outputFile) throws IOException  {		
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
	
	public void generateOutputMessageFile(Path outputFile) throws FileNotFoundException, DocumentException {
		PDFGenerator pdfGen = new PDFGenerator();
		pdfGen.generateMessagesPdfFile(spreadsheet.getMessages(), outputFile);
	}
	
	private void loadDataFromInputSpreadsheet(InputSpreadsheet inputSpreadsheet) {
		for (SjcSpecificCode code : SjcSpecificCode.values()) {
			OutSheet outSheet = new OutSheet(code);
			
			Optional<InSheet> optInSheet = Optional.of(inputSpreadsheet.getSheets().get(code.getGenericCode()));
			if (optInSheet.isPresent() && optInSheet.get().getRows().size() != 0) {				
				InSheet sheet = optInSheet.get();
				List<OutRow> outRows = sheet.getRows().stream()
					.map(inrow -> this.fillOutputRow(inrow, inputSpreadsheet.getLotacao(), code))
					.filter(outRow -> outRow.getQuantidade() != 0)
					.collect(Collectors.toList());
				
				outSheet.getOutputrows().addAll(outRows);									
			}
			this.updateOutputRows(outSheet);
		}
	}
	
	private OutRow fillOutputRow(InRow inRow, String lotacao, SjcSpecificCode code) {
		OutRow outRow = new OutRow(lotacao, inRow.getNome(), inRow.getMatricula());
		if (code.getType() == SjcItemType.HORA_EXTRA) {
			outRow.setQuantidade(inRow.getQtdHoraExtra());
		} else if (code.getType() == SjcItemType.ADICIONAL_NOTURNO) {
			outRow.setQuantidade(inRow.getQtdAdicionalNoturno());
		} else if (code.getType() == SjcItemType.PLANTAO_EXTRA) {
			outRow.setQuantidade(inRow.getQtdPlantoesExtra());
			outRow.setDtPlantoesExtras(inRow.getDtPlantoesExtras());
		}
		return outRow;
	}
	
	private void updateOutputRows(OutSheet outputsheet) {
		if (outputsheet.getOutputrows().isEmpty()) return;
		
		OutSheet sheet = spreadsheet.getSheets().get(outputsheet.getCode());
		
		if (sheet != null ) {
			sheet.getOutputrows().addAll(outputsheet.getOutputrows());
		} else {
			spreadsheet.getSheets().put(outputsheet.getCode(), outputsheet);
		}				
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
