package com.tiagoamp.sjc.model.input;

import static com.tiagoamp.sjc.model.input.InputLayoutConstants.CELL_ADDRESS_ANO_ADMISTRATIVO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.CELL_ADDRESS_ANO_OPERACIONAL;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.CELL_ADDRESS_LOTACAO_ADMISTRATIVO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.CELL_ADDRESS_LOTACAO_OPERACIONAL;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.CELL_ADDRESS_MES_ADMISTRATIVO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.CELL_ADDRESS_MES_OPERACIONAL;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InputSpreadsheet {
	
	public InputSpreadsheet() {
		messages = new ArrayList<>();
		sheets = new ArrayList<>();
	}
	
	private String fileName;
	private String lotacao;
	private List<InSheet> sheets;
	private List<ProcessingMessage> messages;
	private String monthRef;
	private String yearRef;
	
	
	public void loadFromFile(Path inputFile) throws IOException {
		this.fileName = inputFile.getFileName().toString();
		
		if (!validate(inputFile)) return;
		
		try ( FileInputStream fis = new FileInputStream(inputFile.toFile());
			  XSSFWorkbook xssworkbook = new XSSFWorkbook(fis); ) 
			{
			for (SjcGeneralCode code : SjcGeneralCode.values()) {
				XSSFSheet xssfsheet = xssworkbook.getSheet(code.getDescription().toUpperCase());
				if (xssfsheet == null) {
	            	messages.add(new ProcessingMessage(MessageType.ERROR, "Aba '" + code.getDescription().toUpperCase() +"' não encontrada na planilha."));
	            } else {
	            	InSheet sheet = new InSheet(code);
	            	if (lotacao == null) {
	            		CellAddress lotacaoCellAddress = getLotacaoFieldCellAddress(xssfsheet.getSheetName());
	            		lotacao = sheet.loadCellValueFrom(xssfsheet, lotacaoCellAddress);
	            	}
	            	if (monthRef == null) {
	            		CellAddress mesCellAddress = getMesReferenciaFieldCellAddress(xssfsheet.getSheetName());
	            		monthRef = sheet.loadCellValueFrom(xssfsheet, mesCellAddress);
	            	}
	            	if (yearRef == null) {
	            		CellAddress anoCellAddress = getAnoReferenciaFieldCellAddress(xssfsheet.getSheetName());
	            		yearRef = sheet.loadCellValueFrom(xssfsheet, anoCellAddress);	            		
	            	}
	            	sheet.setMonthRef(monthRef);
	            	sheet.setYearRef(yearRef);
	            	sheet.loadDataFrom(xssfsheet);
	            	if (sheet.getInputrows().isEmpty()) continue;
	            	messages.addAll(sheet.getMessages());
	            	sheets.add(sheet);
	            }
			}
			
            if (lotacao == null) {
            	lotacao = "!NOME DA UNIDADE NÃO IDENTIFICADO NA PLANILHA!";
            	messages.add(new ProcessingMessage(MessageType.ERROR, "Não foi identificado o campo 'NOME DA UNIDADE'(no lugar previsto) na planilha."));
            }            
		} 			
	}
	
	public InSheet getInpuSheetFromGenericCode(SjcGeneralCode gCode) {
		for (InSheet inSheet : sheets) {
			if (inSheet.getCode() == gCode) return inSheet;					
		}
		return null;
	}
	
	private boolean validate(Path inputFile) {
		boolean result = true;
		if (Files.isDirectory(inputFile)) {
			messages.add(new ProcessingMessage(MessageType.ERROR, "Arquivo de origem é um diretório e não será considerado no processamento."));
			result = false;
		} else if (!inputFile.getFileName().toString().endsWith(".xlsx")) {
			messages.add(new ProcessingMessage(MessageType.ERROR, "Arquivo de origem não tem extensão '.xlsx'. e não será considerado no processamento."));
			result = false;
		}
		return result;
	}
	
	private CellAddress getLotacaoFieldCellAddress(String sheetName) {
		if (sheetName.equals(SjcGeneralCode.OPERACIONAL.getDescription().toUpperCase())) {
			return new CellAddress(CELL_ADDRESS_LOTACAO_OPERACIONAL);
		} else if (sheetName.equals(SjcGeneralCode.ADMINISTRATIVO.getDescription().toUpperCase())) {
			return new CellAddress(CELL_ADDRESS_LOTACAO_ADMISTRATIVO);
		}
		return null;
	}
	
	private CellAddress getMesReferenciaFieldCellAddress(String sheetName) {
		if (sheetName.equals(SjcGeneralCode.OPERACIONAL.getDescription().toUpperCase())) {
			return new CellAddress(CELL_ADDRESS_MES_OPERACIONAL);
		} else if (sheetName.equals(SjcGeneralCode.ADMINISTRATIVO.getDescription().toUpperCase())) {
			return new CellAddress(CELL_ADDRESS_MES_ADMISTRATIVO);
		}
		return null;
	}
	
	private CellAddress getAnoReferenciaFieldCellAddress(String sheetName) {
		if (sheetName.equals(SjcGeneralCode.OPERACIONAL.getDescription().toUpperCase())) {
			return new CellAddress(CELL_ADDRESS_ANO_OPERACIONAL);
		} else if (sheetName.equals(SjcGeneralCode.ADMINISTRATIVO.getDescription().toUpperCase())) {
			return new CellAddress(CELL_ADDRESS_ANO_ADMISTRATIVO);
		}
		return null;
	}
		
	
	public List<InSheet> getSheets() {
		return sheets;
	}
	public void setSheets(List<InSheet> sheets) {
		this.sheets = sheets;
	}
	public String getLotacao() {
		return lotacao;
	}
	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}
	public List<ProcessingMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ProcessingMessage> messages) {
		this.messages = messages;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMonthRef() {
		return monthRef;
	}
	public void setMonthRef(String monthRef) {
		this.monthRef = monthRef;
	}
	public String getYearRef() {
		return yearRef;
	}
	public void setYearRef(String yearRef) {
		this.yearRef = yearRef;
	}
	
}
