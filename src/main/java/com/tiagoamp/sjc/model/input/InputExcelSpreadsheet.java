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
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;

public class InputExcelSpreadsheet {
	
	private InputSpreadsheet spreadsheet;	
	private Path filePath; 
	
	
	public InputExcelSpreadsheet(Path inputFile) {
		this.filePath = inputFile;
		spreadsheet = new InputSpreadsheet();				
	}
	
	
	public InputSpreadsheet loadFromFile() throws IOException {
		spreadsheet.setFileName(filePath.getFileName().toString());
		
		if (!validate(filePath)) return spreadsheet;
		
		try ( FileInputStream fis = new FileInputStream(filePath.toFile());
			  XSSFWorkbook xssworkbook = new XSSFWorkbook(fis); ) 
			{
			for (SjcGeneralCode code : SjcGeneralCode.values()) {
				XSSFSheet xssfsheet = xssworkbook.getSheet(code.getDescription().toUpperCase());
				
				if (xssfsheet == null) {
					spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ERROR, "Aba com nome '" + code.getDescription().toUpperCase() +"' não encontrada na planilha."));
	            	continue;
	            } 
				
            	loadLotacao(xssfsheet, code);
            	loadYearMonthReference(xssfsheet, code);
            	
            	InExcelSheet excelSheet = new InExcelSheet(code, spreadsheet.getYearMonthRef());
            	excelSheet.loadDataFrom(xssfsheet);
            	InSheet sheet = excelSheet.toInSheet();            	 
            	if (sheet.getRows().isEmpty()) continue;
            	
            	spreadsheet.getSheets().put(code, sheet);            	
			}
			
            if (spreadsheet.getLotacao() == null) {
            	spreadsheet.setLotacao("!NOME DA UNIDADE NÃO IDENTIFICADO NA PLANILHA!");
            	spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ERROR, "Não foi identificado o campo 'NOME DA UNIDADE'(no lugar previsto) na planilha."));
            }            
		}
		return spreadsheet;
	}
	
		
	private boolean validate(Path inputFile) {
		List<ProcessingMessage> validationMsgs = new ArrayList<>();
		if (Files.isDirectory(inputFile)) {
			validationMsgs.add(new ProcessingMessage(MessageType.ERROR, "Arquivo de origem é um diretório e não será considerado no processamento."));
		} else if (!inputFile.getFileName().toString().endsWith(".xlsx")) {
			validationMsgs.add(new ProcessingMessage(MessageType.ERROR, "Arquivo de origem não tem extensão '.xlsx'. e não será considerado no processamento."));
		}
		boolean hasValidationMessages = spreadsheet.getMessages().addAll(validationMsgs);
		return !hasValidationMessages;
	}
	
	private void loadLotacao(XSSFSheet xssfsheet, SjcGeneralCode code) {
		if (spreadsheet.getLotacao() != null) return;
		CellAddress cellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_LOTACAO_OPERACIONAL) : new CellAddress(CELL_ADDRESS_LOTACAO_ADMISTRATIVO);
		String lotacao = xssfsheet.getRow(cellAddr.getRow()).getCell(cellAddr.getColumn()).getStringCellValue();
		spreadsheet.setLotacao(lotacao);
		
	}
	
	private void loadYearMonthReference(XSSFSheet xssfsheet, SjcGeneralCode code) {
		CellAddress yearCellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_ANO_OPERACIONAL) : new CellAddress(CELL_ADDRESS_ANO_ADMISTRATIVO);
		String yearStr = xssfsheet.getRow(yearCellAddr.getRow()).getCell(yearCellAddr.getColumn()).getStringCellValue();
		CellAddress monthCellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_MES_OPERACIONAL) : new CellAddress(CELL_ADDRESS_MES_ADMISTRATIVO);
		String monthStr = xssfsheet.getRow(monthCellAddr.getRow()).getCell(monthCellAddr.getColumn()).getStringCellValue();
		
		YearMonth prevYearMonth = YearMonth.now().minusMonths(1);
		
		int year = prevYearMonth.getYear();
		Pattern nonNumericPattern = Pattern.compile("[^0-9]");
		if (yearStr == null || yearStr.isEmpty() || nonNumericPattern.matcher(yearStr).find()) {
			spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'ANO'(no lugar previsto) na planilha. Assumido ano ref. mês passado."));			
		}
		
		Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(monthStr);
		if (!convertedMonth.isPresent()) {
			spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'MÊS'(no lugar previsto) na planilha. Assumido como mês passado."));
		}
		Month month = convertedMonth.orElse(prevYearMonth.getMonth());
		
		spreadsheet.setYearMonthRef(YearMonth.of(year, month));
	}

}
