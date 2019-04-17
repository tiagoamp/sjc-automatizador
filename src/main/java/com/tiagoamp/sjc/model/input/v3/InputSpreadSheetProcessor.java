package com.tiagoamp.sjc.model.input.v3;

import static com.tiagoamp.sjc.model.MessageType.ALERT;
import static com.tiagoamp.sjc.model.MessageType.ERROR;

import java.time.DateTimeException;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.DataPlantaoFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.FieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.MatriculaFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;
import com.tiagoamp.sjc.model.fieldprocessor.NumericFieldProcessor;

public class InputSpreadSheetProcessor {
	
	public List<ProcessingMessage> process(ConvertedSpreadsheet spreadsheet) {
		List<ProcessingMessage> msgs = new ArrayList<>();		
		List<ProcessingMessage> headerMsgs = validateHeader(spreadsheet.getHeader());
		msgs.addAll(headerMsgs);		
		for (SjcGeneralCode code : SjcGeneralCode.values()) {
			ConvertedSheet sheet = spreadsheet.getConvertedSheets().get(code);			
			if (sheet == null) {
				msgs.add(new ProcessingMessage(ERROR, "Aba com nome '" + code.toString() +"' não encontrada na planilha."));
	        	continue;
	        }	
			sheet.setHeader(spreadsheet.getHeader());  // updated header after validation			
			List<ProcessingMessage> rowsMsgs = validateRows(sheet.getRows(), spreadsheet.getHeader().getYearMonthRef());
			msgs.addAll(rowsMsgs);			
		}
		return msgs;
	}


	private List<ProcessingMessage> validateHeader(ConvHeader header) {
		List<ProcessingMessage> headerMsgs = new ArrayList<>();
				
		if (header.getNomeUnidadePrisional() == null || header.getNomeUnidadePrisional().isEmpty()) {
			headerMsgs.add(new ProcessingMessage(ERROR, "Não foi identificado o Nome da Unidade Prisional.'"));
		}
		
		if (header.getYearMonthRef() != null) return headerMsgs; 
		
		Pattern numericPattern = Pattern.compile("[0-9]");
		String yearStr = header.getYearRefAsStr();		
		boolean isValidYear = yearStr != null && !yearStr.isEmpty() && numericPattern.matcher(yearStr).find();
		if (!isValidYear) {
			headerMsgs.add(new ProcessingMessage(ALERT, "Não foi identificado o campo 'ANO' na planilha. Assumido ano ref mês passado."));			
		}		
		String monthStr = header.getMonthRefAsStr();
		Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(monthStr);
		boolean isValidMonth = convertedMonth.isPresent(); 
		if (!isValidMonth) {
			headerMsgs.add(new ProcessingMessage(ALERT, "Não foi identificado o campo 'MÊS' na planilha. Assumido como mês passado."));			
		}
		
		YearMonth prevYearMonth = YearMonth.now().minusMonths(1);
		if (!isValidYear || !isValidMonth) {
			header.setYearMonthRef(prevYearMonth);
			headerMsgs.add(new ProcessingMessage(ALERT, "Não foi identificado 'ANO' e/ou 'MÊS'. Assumido como planilha do mês passado."));
        	return headerMsgs;
		}
		
		int year = prevYearMonth.getYear(); // get year from last month as default 
		try {
			year = Integer.parseInt(yearStr);	
		} catch (NumberFormatException e) {
			headerMsgs.add(new ProcessingMessage(ALERT, "Não foi possível identificar o 'ANO'. Assumido ano ref mês passado."));
		}
		
		Month month = convertedMonth.get();
		header.setYearMonthRef(YearMonth.of(year, month));
		
		return headerMsgs;
	}
	
	private List<ProcessingMessage> validateRows(List<ConvRow> rows, YearMonth defaultYearMonth) {
		List<ProcessingMessage> rowsMsgs = new ArrayList<>();
		FieldProcessor fieldProcessor;
		int currRowNr = InputLayoutConstants.INDEX_DATA_INIT_ROW; 
				
		Iterator<ConvRow> iterator = rows.iterator();
		while (iterator.hasNext()) {
			ConvRow row = iterator.next();
			currRowNr++;
			
			String matricula = row.getMatricula();
			boolean hasNoNumbers = matricula.matches("^\\D+$");
			if (hasNoNumbers) { 
				rowsMsgs.add(new ProcessingMessage(ERROR, String.format("Matrícula sem nenhum número [%s]. A linha foi desconsiderada! (linha %d)", matricula, currRowNr)));
				iterator.remove();
				continue;
			}
			fieldProcessor = new MatriculaFieldProcessor();
			String validMatricula = fieldProcessor.process(matricula);
			row.setMatricula(validMatricula);
			rowsMsgs.addAll(fieldProcessor.getMessages());
			
			String nome = row.getNome();
			if (nome == null || nome.isEmpty()) 
				rowsMsgs.add(new ProcessingMessage(ERROR, "Nome não identificado: " + nome + " (linha " + currRowNr + ")."));
			
			String value = row.getQtdHoraExtra();
			fieldProcessor = new NumericFieldProcessor("Hora Extra");
			String validNumber = fieldProcessor.process(value);
			row.setQtdHoraExtra(validNumber);
			rowsMsgs.addAll(fieldProcessor.getMessages());
			
			value = row.getQtdAdicionalNoturno();
			fieldProcessor = new NumericFieldProcessor("Adicional Noturno");
			validNumber = fieldProcessor.process(value);
			row.setQtdAdicionalNoturno(validNumber);
			rowsMsgs.addAll(fieldProcessor.getMessages());
			
			value = row.getQtdPlantoesExtra();
			fieldProcessor = new NumericFieldProcessor("Plantão Extra");
			String validQtPltExtras = fieldProcessor.process(value);
			row.setQtdPlantoesExtra(validQtPltExtras);
			rowsMsgs.addAll(fieldProcessor.getMessages());
			
			int qtdDatasPlantInformadas = 0;
			String[] dtPlantoesExtras = row.getDtPlantoesExtras();
			for (int i = 0; i < dtPlantoesExtras.length; i++) {
				String dataPlantaoExtra = dtPlantoesExtras[i];
				boolean isInformedDate = dataPlantaoExtra != null && !dataPlantaoExtra.isEmpty() && dataPlantaoExtra.matches(".*\\d+.*") && !dataPlantaoExtra.equals("0");
				if (isInformedDate) {
					qtdDatasPlantInformadas++;
					try {
						fieldProcessor = new DataPlantaoFieldProcessor(defaultYearMonth);
						String validDatePlExtra = fieldProcessor.process(dataPlantaoExtra);
						row.getDtPlantoesExtras()[i] = validDatePlExtra;
					} catch (DateTimeException e) {
						String msg = String.format("Planilha contém 'Data de Plantão Extra' com formato não reconhecido: %s . Formato recomendado = 'dd/mm/aaaa'. (linha %d)", dataPlantaoExtra, currRowNr);
	        			rowsMsgs.add(new ProcessingMessage(ERROR, msg));	        			
	        		}
					
				}
			}
			if (row.getQtdPlantoesExtra() != null && !row.getQtdPlantoesExtra().isEmpty() && 
				Integer.valueOf(row.getQtdPlantoesExtra()) != qtdDatasPlantInformadas) {
				String msg = String.format("Planilha contém 'Qtdade de Plantões Extras' diferente do número de datas. Cadastradas %s datas mas quantidade informada é %s (linha %d)", qtdDatasPlantInformadas, row.getQtdPlantoesExtra(), currRowNr);
        		rowsMsgs.add(new ProcessingMessage(ALERT, msg));
        	}			
		}
		
		return rowsMsgs;
	}
	
}
