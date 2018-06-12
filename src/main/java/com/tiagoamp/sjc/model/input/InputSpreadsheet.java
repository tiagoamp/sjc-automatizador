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
import java.util.Optional;

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
	            	messages.add(new ProcessingMessage(MessageType.ERROR, "Aba com nome '" + code.getDescription().toUpperCase() +"' não encontrada na planilha."));
	            	continue;
	            } 
				
            	loadLotacao(xssfsheet, code);
            	loadMesReferencia(xssfsheet, code);
            	loadAnoReferencia(xssfsheet, code);
            	
            	InSheet sheet = new InSheet(code, monthRef, yearRef);            	
            	sheet.loadDataFrom(xssfsheet);
            	
            	if (sheet.getInputrows().isEmpty()) continue;
            	messages.addAll(sheet.getMessages());
            	sheets.add(sheet);	            
			}
			
            if (lotacao == null) {
            	lotacao = "!NOME DA UNIDADE NÃO IDENTIFICADO NA PLANILHA!";
            	messages.add(new ProcessingMessage(MessageType.ERROR, "Não foi identificado o campo 'NOME DA UNIDADE'(no lugar previsto) na planilha."));
            }            
		} 			
	}
	
	public Optional<InSheet> getInpuSheetFromGenericCode(SjcGeneralCode code) {
		return sheets.stream().filter(sheet -> sheet.getCode() == code).findFirst();		
	}
	
	
	private boolean validate(Path inputFile) {
		List<ProcessingMessage> validationMsgs = new ArrayList<>();
		if (Files.isDirectory(inputFile)) {
			validationMsgs.add(new ProcessingMessage(MessageType.ERROR, "Arquivo de origem é um diretório e não será considerado no processamento."));
		} else if (!inputFile.getFileName().toString().endsWith(".xlsx")) {
			validationMsgs.add(new ProcessingMessage(MessageType.ERROR, "Arquivo de origem não tem extensão '.xlsx'. e não será considerado no processamento."));
		}
		boolean hasValidationMessages = messages.addAll(validationMsgs);
		return !hasValidationMessages;
	}
	
	private void loadLotacao(XSSFSheet xssfsheet, SjcGeneralCode code) {
		if (lotacao != null) return;
		CellAddress cellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_LOTACAO_OPERACIONAL) : new CellAddress(CELL_ADDRESS_LOTACAO_ADMISTRATIVO);
		lotacao = xssfsheet.getRow(cellAddr.getRow()).getCell(cellAddr.getColumn()).getStringCellValue();
	}
	
	private void loadMesReferencia(XSSFSheet xssfsheet, SjcGeneralCode code) {
		if (lotacao != null) return;
		CellAddress cellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_MES_OPERACIONAL) : new CellAddress(CELL_ADDRESS_MES_ADMISTRATIVO);
		monthRef = xssfsheet.getRow(cellAddr.getRow()).getCell(cellAddr.getColumn()).getStringCellValue();
	}
	
	private void loadAnoReferencia(XSSFSheet xssfsheet, SjcGeneralCode code) {
		if (lotacao != null) return;
		CellAddress cellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_ANO_OPERACIONAL) : new CellAddress(CELL_ADDRESS_ANO_ADMISTRATIVO);
		yearRef = xssfsheet.getRow(cellAddr.getRow()).getCell(cellAddr.getColumn()).getStringCellValue();
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
