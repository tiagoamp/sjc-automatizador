package com.tiagoamp.sjc.model.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.tiagoamp.sjc.model.SjcItemType;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.model.input.AfastamentoRow;
import com.tiagoamp.sjc.model.input.HistoricoAfastamentos;
import com.tiagoamp.sjc.model.input.InRow;
import com.tiagoamp.sjc.model.input.InSheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;

public class OutputExcelSpreadsheet {
	
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
						.filter(afastRow -> (plantaoDate.isAfter(afastRow.getDataInicial())	|| plantaoDate.isEqual(afastRow.getDataInicial()))
								         && (plantaoDate.isBefore(afastRow.getDataFinal()) || plantaoDate.isEqual(afastRow.getDataFinal())))
						.count();
				
				if (countOfAfastConflicts == 0) outRow.getDtPlantoesWithinAfastamentos()[i] = false;
				else outRow.getDtPlantoesWithinAfastamentos()[i] = true;

			} catch (Exception e) {
				outRow.getDtPlantoesWithinAfastamentos()[i] = null;
			}

		}
		
		return outRow;
	}
	
	private void updateOutputRows(OutSheet outputsheet) {
		if (outputsheet.getOutputrows().isEmpty()) return;
		
		OutSheet sheet = spreadsheet.getSheets().get(outputsheet.getCode());
		
		if (sheet != null ) {
			sheet.getOutputrows().addAll(outputsheet.getOutputrows());
		} else {
			spreadsheet.getSheets().put(outputsheet.getCode(), outputsheet);
		}				
	}
	
}
