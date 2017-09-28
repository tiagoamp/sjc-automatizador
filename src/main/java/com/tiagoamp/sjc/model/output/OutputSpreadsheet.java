package com.tiagoamp.sjc.model.output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tiagoamp.sjc.model.input.InRow;
import com.tiagoamp.sjc.model.input.InSheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;

import com.tiagoamp.sjc.model.LotacaoComparator;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcItemType;
import com.tiagoamp.sjc.model.SjcSpecificCode;

public class OutputSpreadsheet {
	
	public OutputSpreadsheet() {
		sheets = new ArrayList<>();
		messages = new HashMap<>();
	}
	
	
	private String lotacao;
	private List<OutSheet> sheets;
	private Map<String,List<ProcessingMessage>> messages;
	
	private Path templateFile = Paths.get("src","main","resources","sjc","template_output.xlsx");
	
	
	public void loadDataFromInputSpreadSheets(List<InputSpreadsheet> listInputSpreadSheets) {
		for (InputSpreadsheet inputSpreadsheet : listInputSpreadSheets) {
			loadDataFromInputSpreadsheet(inputSpreadsheet);
			messages.put(inputSpreadsheet.getFileName(), inputSpreadsheet.getMessages());			
		}	
	}
	
	public void generateOuputSpreadsheetFile(Path outputFile) throws IOException  {
		createOutputFileInSystem(outputFile, templateFile);
		populateOutputFile(outputFile);
	}
	
	public void generateOutputMessagesPage(Path outputFile) throws IOException {
		String htmlCode = new HtmlMessagePage().generate(messages);
		createOutputMessageFileInSystem(outputFile, htmlCode);
	}	
	
	private void loadDataFromInputSpreadsheet(InputSpreadsheet inputSpreadsheet) {
		for (SjcSpecificCode code : SjcSpecificCode.values()) {
			OutSheet outSheet = new OutSheet(code);
			InSheet inSheet = inputSpreadsheet.getInpuSheetFromGenericCode(code.getGenericCode());
			if (inSheet != null && inSheet.getInputrows().size() != 0) {
				for (InRow inrow : inSheet.getInputrows()) { // for each input row
					OutRow outRow = this.fillRow(inrow, inputSpreadsheet.getLotacao(), code);
					if (outRow.getQuantidade() != 0) {
						outSheet.getOutputrows().add(outRow);
					}
				}					
			}
			this.updateOutputRows(outSheet);
		}
	}
	
	private OutRow fillRow(InRow inRow, String lotacao, SjcSpecificCode code) {
		OutRow outRow = new OutRow(lotacao, inRow.getNome(), inRow.getMatricula());
		if (code.getType() == SjcItemType.HORA_EXTRA) {
			outRow.setQuantidade(inRow.getQtdHoraExtra());
		} else if (code.getType() == SjcItemType.ADICIONAL_NOTURNO) {
			outRow.setQuantidade(inRow.getQtdAdicionalNoturno());
		} else if (code.getType() == SjcItemType.PLANTAO_EXTRA) {
			outRow.setQuantidade(inRow.getQtdPlantoesExtra());
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
		for(int index=0; index < sheets.size(); index++) {
			if (sheets.get(index).getCode() == code) return index;
		}
		return -1;
	}
	
	private void createOutputFileInSystem(Path outputFile, Path templateFile) throws IOException {
		Files.copy(templateFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
	}
	
	private void createOutputMessageFileInSystem(Path outputFile, String htmlCode) throws IOException {
		try (FileOutputStream fos = new FileOutputStream(outputFile.toFile());) {
			fos.write(htmlCode.getBytes());
		} 
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
				
				for (int j = 0; j < 4; j++) {
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
			data.put(counter, new Object[] {outRow.getLotacao(), outRow.getNome(), outRow.getMatricula(), outRow.getQuantidade()});
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
	
	
	public String getLotacao() {
		return lotacao;
	}
	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
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