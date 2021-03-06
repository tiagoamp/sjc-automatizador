package com.tiagoamp.sjc.model.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiagoamp.sjc.model.SjcItemType;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.model.input.AfastamentoRow;
import com.tiagoamp.sjc.model.input.HistoricoAfastamentos;
import com.tiagoamp.sjc.model.input.InRow;
import com.tiagoamp.sjc.model.input.InSheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.input.v3.ConvRow;
import com.tiagoamp.sjc.model.input.v3.ConvertedSheet;
import com.tiagoamp.sjc.model.input.v3.ConvertedSpreadsheet;

public class OutputExcelSpreadsheet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OutputExcelSpreadsheet.class);
	
	private OutputSpreadsheet spreadsheet;
	
	
	public OutputExcelSpreadsheet() {
		spreadsheet = new OutputSpreadsheet();
	}
	

	public OutputSpreadsheet loadDataFromInputSpreadsheets(List<InputSpreadsheet> inputSpreadsheets, HistoricoAfastamentos afastamentos) {
		inputSpreadsheets.forEach(inputSpreadsheet -> {
			loadDataFromInputSpreadsheet(inputSpreadsheet, afastamentos);			
			spreadsheet.getMessages().put(inputSpreadsheet.getFileName(), inputSpreadsheet.getMessages());
		}); 
		return spreadsheet;
	}
	
	public OutputSpreadsheet loadDataFromConvertedSpreadsheets(List<ConvertedSpreadsheet> convSpreadsheets, HistoricoAfastamentos afastamentos) {
		convSpreadsheets.forEach(convSpreadsheet -> loadDataFromConvertedInputSpreadsheet(convSpreadsheet, afastamentos)); 
		return spreadsheet;
	}
	
	@Deprecated	
	private void loadDataFromInputSpreadsheet(InputSpreadsheet inputSpreadsheet, HistoricoAfastamentos afastamentos) {
		if (inputSpreadsheet.getSheets().isEmpty()) return;
		
		for (SjcSpecificCode code : SjcSpecificCode.values()) {
			OutSheet outSheet = new OutSheet(code);			
			Optional<InSheet> optInSheet = Optional.ofNullable(inputSpreadsheet.getSheets().get(code.getGenericCode()));
			if (optInSheet.isPresent() && optInSheet.get().getRows().size() != 0) {				
				InSheet sheet = optInSheet.get();
				List<OutRow> outRows = sheet.getRows().stream()
					.map(inrow -> this.fillOutputRow(inrow, inputSpreadsheet.getLotacao(), code))
					.filter(outRow -> outRow.getQuantidade() != 0)
					.collect(Collectors.toList());
				
				if (code == SjcSpecificCode.OPERACIONAL_PLANTOESEXTRA) {
					outRows = outRows.stream().map(outrow -> this.fillAfastamentos(outrow, afastamentos)).collect(Collectors.toList());
				}
				
				outSheet.getRows().addAll(outRows);									
			}
			this.updateOutputRows(outSheet);
		}
	}
	
	private void loadDataFromConvertedInputSpreadsheet(ConvertedSpreadsheet convSpreadsheet, HistoricoAfastamentos afastamentos) {
		if (convSpreadsheet.getConvertedSheets().isEmpty()) return;
		String nmUnidPrisional = convSpreadsheet.getHeader().getNomeUnidadePrisional();
		if ( nmUnidPrisional == null || nmUnidPrisional.isEmpty()) {
			convSpreadsheet.getHeader().setNomeUnidadePrisional("(Não consta na planilha convertida). Arquivo: " + convSpreadsheet.getConvertedFile().getFileName().toString());
		}
		for (SjcSpecificCode code : SjcSpecificCode.values()) {
			OutSheet outSheet = new OutSheet(code);			
			Optional<ConvertedSheet> optInSheet = Optional.ofNullable(convSpreadsheet.getConvertedSheets().get(code.getGenericCode()));
			if (optInSheet.isPresent() && optInSheet.get().getRows().size() != 0) {
				ConvertedSheet cnvSheet = optInSheet.get();				
				List<OutRow> outRows = cnvSheet.getRows().stream()
					.map(inrow -> fillOutputRow(inrow, convSpreadsheet.getHeader().getNomeUnidadePrisional(), code))
					.filter(outrow -> outrow.getQuantidade() != 0)
					.collect(Collectors.toList());
				if (code == SjcSpecificCode.OPERACIONAL_PLANTOESEXTRA) {
					outRows = outRows.stream().map(outrow -> this.fillAfastamentos(outrow, afastamentos)).collect(Collectors.toList());
				}
				outSheet.getRows().addAll(outRows);
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
	
	private OutRow fillOutputRow(ConvRow cnvRow, String nomeUnidade, SjcSpecificCode code) {
		OutRow outRow = new OutRow(nomeUnidade, cnvRow.getNome(), cnvRow.getMatricula());
		if (code.getType() == SjcItemType.HORA_EXTRA) {
			outRow.setQuantidade(Integer.valueOf(cnvRow.getQtdHoraExtra()));
		} else if (code.getType() == SjcItemType.ADICIONAL_NOTURNO) {
			outRow.setQuantidade(Integer.valueOf(cnvRow.getQtdAdicionalNoturno()));
		} else if (code.getType() == SjcItemType.PLANTAO_EXTRA) {
			outRow.setQuantidade(Integer.valueOf(cnvRow.getQtdPlantoesExtra()));
			outRow.setDtPlantoesExtras(cnvRow.getDtPlantoesExtras());
		}
		return outRow;
	}
	
	private OutRow fillAfastamentos(OutRow outRow, HistoricoAfastamentos afastamentos) {
		if (afastamentos == null) return outRow;
		
		List<AfastamentoRow> afastamentosFromThisMatricula = afastamentos.getSheet().getRows().stream()
			.filter(afastRow -> afastRow.getMatricula().equals(outRow.getMatricula()))
			.collect(Collectors.toList());
		
		if (afastamentosFromThisMatricula.isEmpty()) return outRow;
		
		afastamentosFromThisMatricula.forEach(afast -> {
			String afastamentosConcat = outRow.getAfastamento() == null ? afast.getAfastamentoForOuputRow() : outRow.getAfastamento() + " | " + afast.getAfastamentoForOuputRow();
			outRow.setAfastamento(afastamentosConcat);
		});
		
		final String REGEX_FULL_DATE = "\\d{1,2}[-|\\/]{1}[\\w]+[-|\\/]\\d+";
		String[] dtPlantoesExtras = outRow.getDtPlantoesExtras();
		
		for (int i = 0; i < dtPlantoesExtras.length; i++) {
			String dateStr = dtPlantoesExtras[i];
			if (StringUtils.isEmpty(dateStr) || !dateStr.matches(REGEX_FULL_DATE)) {
				outRow.getDtPlantoesWithinAfastamentos()[i] = null;  // not identified
				return outRow;
			}

			String[] splitDateArr = dateStr.split("/");
			try {
				final LocalDate plantaoDate = LocalDate.of(Integer.parseInt(splitDateArr[2]), Integer.parseInt(splitDateArr[1]), Integer.parseInt(splitDateArr[0]));
				long countOfAfastConflicts = afastamentosFromThisMatricula.stream()
						.filter(afastRow -> 
									(plantaoDate.isAfter(afastRow.getDataInicial())	|| plantaoDate.isEqual(afastRow.getDataInicial()))
								 && (plantaoDate.isBefore(afastRow.getDataFinal()) || plantaoDate.isEqual(afastRow.getDataFinal())))
						.count();
				outRow.getDtPlantoesWithinAfastamentos()[i] = countOfAfastConflicts != 0;				
			} catch (Exception e) {
				LOGGER.debug("Padrão de data não reconhecido: " + dateStr);
				outRow.getDtPlantoesWithinAfastamentos()[i] = null;
			}

		}
		
		return outRow;
	}
	
	private void updateOutputRows(OutSheet outputsheet) {
		if (outputsheet.getRows().isEmpty()) return;
		
		OutSheet sheet = spreadsheet.getSheets().get(outputsheet.getCode());
		
		if (sheet != null ) {
			sheet.getRows().addAll(outputsheet.getRows());
		} else {
			spreadsheet.getSheets().put(outputsheet.getCode(), outputsheet);
		}				
	}
	
}
