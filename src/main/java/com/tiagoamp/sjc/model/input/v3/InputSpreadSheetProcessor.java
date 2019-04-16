package com.tiagoamp.sjc.model.input.v3;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;
import com.tiagoamp.sjc.model.input.InExcelSheet;
import com.tiagoamp.sjc.model.input.InSheet;

public class InputSpreadSheetProcessor {
	
		
	public List<ProcessingMessage> process(ConvertedSpreadsheet spreadsheet) {
		List<ProcessingMessage> msgs = new ArrayList<>();
		
		validateHeader(spreadsheet.getHeader());
		
		for (SjcGeneralCode code : SjcGeneralCode.values()) {
			ConvertedSheet sheet = spreadsheet.getConvertedSheets().get(code);
			
			if (sheet == null) {
				msgs.add(new ProcessingMessage(MessageType.ERROR, "Aba com nome '" + code.toString() +"' não encontrada na planilha."));
	        	continue;
	        }
			
			
			
		}
		
 
		
		
	}

	
	private List<ProcessingMessage> validateHeader(ConvHeader header) {
		List<ProcessingMessage> headerMsgs = new ArrayList<>();
				
		if (header.getNomeUnidadePrisional() == null || header.getNomeUnidadePrisional().isEmpty()) {
			headerMsgs.add(new ProcessingMessage(MessageType.ERROR, "Não foi identificado o Nome da Unidade Prisional.'"));
		}
		
		if (header.getYearMonthRef() != null) return headerMsgs; 
		
		Pattern numericPattern = Pattern.compile("[0-9]");
		String yearStr = header.getYearRefAsStr();		
		boolean isValidYear = yearStr != null && !yearStr.isEmpty() && numericPattern.matcher(yearStr).find();
		if (!isValidYear) {
			headerMsgs.add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'ANO' na planilha. Assumido ano ref mês passado."));			
		}		
		String monthStr = header.getMonthRefAsStr();
		Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(monthStr);
		boolean isValidMonth = convertedMonth.isPresent(); 
		if (!isValidMonth) {
			headerMsgs.add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado o campo 'MÊS' na planilha. Assumido como mês passado."));			
		}
		
		YearMonth prevYearMonth = YearMonth.now().minusMonths(1);
		if (!isValidYear || !isValidMonth) {
			header.setYearMonthRef(prevYearMonth);
			headerMsgs.add(new ProcessingMessage(MessageType.ALERT, "Não foi identificado 'ANO' e/ou 'MÊS'. Assumido como planilha do mês passado."));
        	return headerMsgs;
		}
		
		int year = prevYearMonth.getYear(); // get year from last month as default 
		try {
			year = Integer.parseInt(yearStr);	
		} catch (NumberFormatException e) {
			headerMsgs.add(new ProcessingMessage(MessageType.ALERT, "Não foi possível identificar o 'ANO'. Assumido ano ref mês passado."));
		}
		
		Month month = convertedMonth.get();
		header.setYearMonthRef(YearMonth.of(year, month));
		
		return headerMsgs;
	}
	
	private List<ProcessingMessage> validateRows(List<ConvRow> rows) {
		
	}
	
}
