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
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;

public class InputSpreadsheet {
	
	public InputSpreadsheet() {
		messages = new ArrayList<>();
		sheets = new HashMap<>();
	}
	
	private String fileName;
	private String lotacao;
	private YearMonth yearMonthRef;
	private Map<SjcGeneralCode, InSheet> sheets;
	private List<ProcessingMessage> messages;	
	
	
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
            	loadYearMonthReference(xssfsheet, code);
            	
            	InSheet sheet = new InSheet(code, yearMonthRef);            	
            	sheet.loadDataFrom(xssfsheet);
            	
            	if (sheet.getRows().isEmpty()) continue;
            	messages.addAll(sheet.getMessages());
            	sheets.put(sheet.getCode(), sheet);
			}
			
            if (lotacao == null) {
            	lotacao = "!NOME DA UNIDADE NÃO IDENTIFICADO NA PLANILHA!";
            	messages.add(new ProcessingMessage(MessageType.ERROR, "Não foi identificado o campo 'NOME DA UNIDADE'(no lugar previsto) na planilha."));
            }            
		} 			
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
	
	private void loadYearMonthReference(XSSFSheet xssfsheet, SjcGeneralCode code) {
		CellAddress yearCellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_ANO_OPERACIONAL) : new CellAddress(CELL_ADDRESS_ANO_ADMISTRATIVO);
		String yearStr = xssfsheet.getRow(yearCellAddr.getRow()).getCell(yearCellAddr.getColumn()).getStringCellValue();
		CellAddress monthCellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_MES_OPERACIONAL) : new CellAddress(CELL_ADDRESS_MES_ADMISTRATIVO);
		String monthStr = xssfsheet.getRow(monthCellAddr.getRow()).getCell(monthCellAddr.getColumn()).getStringCellValue();
		
		LocalDate prevMonthDate = LocalDate.now().minusMonths(1);
		
		int year = prevMonthDate.getYear();
		Pattern nonNumericPattern = Pattern.compile("[^0-9]");
		if (yearStr == null || yearStr.isEmpty() || nonNumericPattern.matcher(yearStr).find()) {
			messages.add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'ANO'(no lugar previsto) na planilha. Assumido ano ref. mês passado."));			
		}
		
		Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(monthStr);
		if (!convertedMonth.isPresent()) messages.add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'MÊS'(no lugar previsto) na planilha. Assumido como mês passado."));
		Month month = convertedMonth.orElse(prevMonthDate.getMonth());
		
		yearMonthRef = YearMonth.of(year, month);
	}
	
	
	public Map<SjcGeneralCode, InSheet> getSheets() {
		return sheets;
	}
	public void setSheets(Map<SjcGeneralCode, InSheet> sheets) {
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
	public YearMonth getYearMonthRef() {
		return yearMonthRef;
	}
	public void setYearMonthRef(YearMonth yearMonthRef) {
		this.yearMonthRef = yearMonthRef;
	}
	
}
