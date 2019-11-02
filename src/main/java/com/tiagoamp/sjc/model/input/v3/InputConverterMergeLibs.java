package com.tiagoamp.sjc.model.input.v3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InputConverterMergeLibs implements IInputConverter {
	
	private Path file;
	
	
	public InputConverterMergeLibs(Path file) {
		this.file = file;
	}
	
		
	public ConvertedSpreadsheet convert() throws IOException {		
		InputConverterEadgyoLib converterEadgyoLib = new InputConverterEadgyoLib(file);
		InputConverterITextLib converterITextLib = new InputConverterITextLib(file);
		
		ConvertedSpreadsheet convEadSpreadSheet = converterEadgyoLib.convert();
		ConvertedSpreadsheet convItextSpreadSheet = converterITextLib.convert();
		
		for(SjcGeneralCode code : SjcGeneralCode.values()) {
			ConvertedSheet convEad = convEadSpreadSheet.getConvertedSheets().get(code);
			ConvertedSheet convItext = convItextSpreadSheet.getConvertedSheets().get(code);
			
			for (ConvRow row : convEad.getRows()) {
				if (!row.shouldCkeckInfo()) continue;
				Optional<ConvRow> other = convItext.getRows().stream().
						filter(r -> r.getMatricula().equals(row.getMatricula())).findFirst();
				if (!other.isPresent()) continue;
				ConvRow otherRow = other.get();
				
				int qtdPlantoesOther = otherRow.getQtdPlantoesExtra() != null ? Integer.valueOf(otherRow.getQtdPlantoesExtra()) : 0;
				int qtdPlantoesRow = row.getQtdPlantoesExtra() != null ? Integer.valueOf(row.getQtdPlantoesExtra()) : 0;
				/*if (qtdPlantoesRow == qtdPlantoesOther && qtdPlantoesRow == 0) {  
					if (row.getQtdHoraExtra() != otherRow.getQtdHoraExtra() && otherRow.getQtdHoraExtra() != null) 
						row.setQtdHoraExtra(otherRow.getQtdHoraExtra());
					if (row.getQtdAdicionalNoturno() != otherRow.getQtdAdicionalNoturno() && otherRow.getQtdAdicionalNoturno() != null) 
						row.setQtdAdicionalNoturno(otherRow.getQtdAdicionalNoturno());
				} else*/ 
				if (qtdPlantoesOther > qtdPlantoesRow) {  
					row.setDtPlantoesExtras(otherRow.getDtPlantoesExtras());
					row.setQtdPlantoesExtra(otherRow.getQtdPlantoesExtra());
				} 				
			}
		}
		
		return convEadSpreadSheet;
	}
	
}
