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

import org.apache.poi.ss.usermodel.DataFormatter;
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
            	InSheet sheet = excelSheet.loadDataFrom(xssfsheet);
            	if (sheet.getRows().isEmpty()) continue;
            	
            	spreadsheet.getSheets().put(code, sheet);
            	spreadsheet.getMessages().addAll(sheet.getMessages());            	
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
		} else if (!inputFile.getFileName().toString().toLowerCase().endsWith(".xlsx")) {
			validationMsgs.add(new ProcessingMessage(MessageType.ERROR, "Arquivo de origem não tem extensão '.xlsx'. e não será considerado no processamento."));
		}
		boolean hasValidationMessages = spreadsheet.getMessages().addAll(validationMsgs);
		return !hasValidationMessages;
	}
	
	private void loadLotacao(XSSFSheet xssfsheet, SjcGeneralCode code) {
		if (spreadsheet.getLotacao() != null) return;
		CellAddress cellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_LOTACAO_OPERACIONAL) : new CellAddress(CELL_ADDRESS_LOTACAO_ADMISTRATIVO);
		DataFormatter df = new DataFormatter();
		String lotacao = df.formatCellValue(xssfsheet.getRow(cellAddr.getRow()).getCell(cellAddr.getColumn()));
		spreadsheet.setLotacao(lotacao);		
	}
	
	private void loadYearMonthReference(XSSFSheet xssfsheet, SjcGeneralCode code) {
		DataFormatter df = new DataFormatter();
		CellAddress yearCellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_ANO_OPERACIONAL) : new CellAddress(CELL_ADDRESS_ANO_ADMISTRATIVO);
		CellAddress monthCellAddr = code == SjcGeneralCode.OPERACIONAL ? new CellAddress(CELL_ADDRESS_MES_OPERACIONAL) : new CellAddress(CELL_ADDRESS_MES_ADMISTRATIVO);
		
		boolean existsYearAndMonthRowsInSheet = xssfsheet.getRow(yearCellAddr.getRow()) != null && xssfsheet.getRow(monthCellAddr.getRow()) != null; 
		
		String yearStr = null;
		String monthStr = null;
		if (existsYearAndMonthRowsInSheet) {
			yearStr = df.formatCellValue(xssfsheet.getRow(yearCellAddr.getRow()).getCell(yearCellAddr.getColumn()));
			monthStr = df.formatCellValue(xssfsheet.getRow(monthCellAddr.getRow()).getCell(monthCellAddr.getColumn()));
		}
				
		Pattern numericPattern = Pattern.compile("[0-9]");
		boolean isValidYear = yearStr != null && !yearStr.isEmpty() && numericPattern.matcher(yearStr).find();
		if (!isValidYear) {
			spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'ANO' (no lugar previsto, célula '" + yearCellAddr.formatAsString() + "') na planilha na aba '" + code.getDescription() + "'. Assumido ano ref mês passado."));			
		}		
		Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(monthStr);
		boolean isValidMonth = convertedMonth.isPresent(); 
		if (!isValidMonth) {
			spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'MÊS' (no lugar previsto, célula '" + monthCellAddr.formatAsString() + "') na planilha na aba '" + code.getDescription() + "'. Assumido como mês passado."));			
		}
		
		YearMonth prevYearMonth = YearMonth.now().minusMonths(1);
		if (!isValidYear || !isValidMonth) {
			spreadsheet.setYearMonthRef(prevYearMonth);
        	spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado 'ANO' e/ou 'MÊS' (no lugar previsto) na aba '" + code.getDescription() + "'. Assumido como planilha do mês passado."));
        	return;
		}
		
		int year = prevYearMonth.getYear(); // get year from last month as default 
		try {
			year = Integer.parseInt(yearStr);	
		} catch (NumberFormatException e) {
			spreadsheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Não foi possível identificar o 'ANO' (no lugar previsto, célula '" + yearCellAddr.formatAsString() + ") na planilha na aba '" + code.getDescription() + "'. Assumido ano ref mês passado."));
		}
		
		Month month = convertedMonth.get();
		spreadsheet.setYearMonthRef(YearMonth.of(year, month));		
	}

}
