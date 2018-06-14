package com.tiagoamp.sjc.model.output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.LotacaoComparator;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcItemType;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.model.input.InRow;
import com.tiagoamp.sjc.model.input.InSheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.service.PDFGenerator;

public class OutputSpreadsheet {
	
	public OutputSpreadsheet() {
		sheets = new ArrayList<>();
		messages = new HashMap<>();
	}
	
	
	private List<OutSheet> sheets;
	private Map<String,List<ProcessingMessage>> messages;
	
	
	public void loadDataFromInputSpreadSheets(List<InputSpreadsheet> listInputSpreadSheets) {
		listInputSpreadSheets.forEach(inputSpreadsheet -> {
			loadDataFromInputSpreadsheet(inputSpreadsheet);
			messages.put(inputSpreadsheet.getFileName(), inputSpreadsheet.getMessages());
		});			
	}
	
	public void generateOuputSpreadsheetFile(Path outputFile, Path templateFile) throws IOException  {		
		createOutputFileInSystem(outputFile, templateFile);
		populateOutputFile(outputFile);
	}
	
	public void generateOutputMessageFile(Path outputFile) throws FileNotFoundException, DocumentException {
		PDFGenerator pdfGen = new PDFGenerator();
		pdfGen.generateMessagesPdfFile(messages, outputFile);
	}
	
	private void loadDataFromInputSpreadsheet(InputSpreadsheet inputSpreadsheet) {
		for (SjcSpecificCode code : SjcSpecificCode.values()) {
			OutSheet outSheet = new OutSheet(code);
			
			Optional<InSheet> inSheet = Optional.of(inputSpreadsheet.getSheets().get(code.getGenericCode()));
			if (inSheet.isPresent() && inSheet.get().getRows().size() != 0) {
				
				InSheet sheet = inSheet.get();
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
		
		int index = this.getSheetIndexByCode(outputsheet.getCode());
		if (index >= 0) {
			sheets.get(index).getOutputrows().addAll(outputsheet.getOutputrows());			
		} else {
			sheets.add(outputsheet);
		}		
	}
		
	private int getSheetIndexByCode(SjcSpecificCode code) {
		return IntStream.range(0, sheets.size())
				.filter(index -> sheets.get(index).getCode() == code)
				.findFirst()
				.orElse(-1);
	}
	
	private void createOutputFileInSystem(Path outputFile, Path templateFile) throws IOException {
		Files.copy(templateFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
	}
	
	private void populateOutputFile(Path outputFile) throws FileNotFoundException, IOException {
		try (FileInputStream fis = new FileInputStream(outputFile.toFile());
			 XSSFWorkbook xssfworkbook = new XSSFWorkbook(fis);
			 FileOutputStream fos = new FileOutputStream(outputFile.toFile());  
			) 
			{
			for (SjcSpecificCode code : SjcSpecificCode.values()) {
				OutSheet sheet = null;
				int index = this.getSheetIndexByCode(code);
				if (index >= 0) sheet = sheets.get(index);
				if (sheet == null || sheet.getOutputrows() == null || sheet.getOutputrows().size() == 0) continue;
				Collections.sort(sheet.getOutputrows(), new LotacaoComparator());
		    			    		
				Map<Integer, Object[]> data = this.createOutputDataMap(sheet);
						
				XSSFSheet xssfsheet = xssfworkbook.getSheet(String.valueOf(code.getCode()));
				this.fillNewOuputRowsInExcelSheet(xssfsheet, data);
				
				for (int j = 0; j < 9; j++) {
					xssfsheet.autoSizeColumn(j); // column adjusting
				}
			}
			xssfworkbook.write(fos);
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
	
	
	public List<OutSheet> getSheets() {
		return sheets;
	}
	public void setSheets(List<OutSheet> sheets) {
		this.sheets = sheets;
	}
	public Map<String, List<ProcessingMessage>> getMessages() {
		return messages;
	}

	
}
