package com.tiagoamp.sjc.model.output;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tiagoamp.sjc.model.SjcItemType;
import com.tiagoamp.sjc.model.SjcSpecificCode;
import com.tiagoamp.sjc.model.input.InRow;
import com.tiagoamp.sjc.model.input.InSheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;

public class OutputExcelSpreadsheet {
	
	private OutputSpreadsheet spreadsheet;
	
	
	public OutputExcelSpreadsheet() {
		spreadsheet = new OutputSpreadsheet();
	}
	

	public OutputSpreadsheet loadDataFromInputSpreadsheets(List<InputSpreadsheet> inputSpreadsheets) {		
		inputSpreadsheets.forEach(inputSpreadsheet -> {
			loadDataFromInputSpreadsheet(inputSpreadsheet);			
			spreadsheet.getMessages().put(inputSpreadsheet.getFileName(), inputSpreadsheet.getMessages());
		}); 
		return spreadsheet;
	}
	
		
	private void loadDataFromInputSpreadsheet(InputSpreadsheet inputSpreadsheet) {
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
