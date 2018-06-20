package com.tiagoamp.sjc.model.output;

import java.util.ArrayList;
import java.util.List;

import com.tiagoamp.sjc.model.SjcSpecificCode;

public class OutSheet {
	
	private SjcSpecificCode code;
	private List<OutRow> outputrows;

	
	public OutSheet(SjcSpecificCode code) {
		this.code = code;
		outputrows = new ArrayList<>();
	}
	
	
	public void sortRows() {
		sortRowsByLotacao();
		groupRowsByMatricula();		
	}
	
	
	private void sortRowsByLotacao() {
		outputrows.sort((r1,r2) -> r1.getLotacao().compareTo(r2.getLotacao()));
	}
	
	private void groupRowsByMatricula() {
		List<OutRow> groupedRows = new ArrayList<>(outputrows); // creates new List from existing output rows
		
		for (int i = 0; i < outputrows.size(); i++) {
			OutRow currRow = outputrows.get(i);
			
			for (int j = i+1; j < outputrows.size(); j++) {
				OutRow nextRow = outputrows.get(j);
				
				if (currRow.getMatricula().equals(nextRow.getMatricula())) {
					groupedRows.remove(j);
					groupedRows.add(i+1, nextRow);
					i++;  // to not process inserted repated element
				}				
			}			
		}
		
		outputrows = groupedRows;
	}
	
	
	public SjcSpecificCode getCode() {
		return code;
	}
	public void setCode(SjcSpecificCode specificCode) {
		this.code = specificCode;
	}
	public List<OutRow> getOutputrows() {
		return outputrows;
	}
	public void setOutputrows(List<OutRow> outputrows) {
		this.outputrows = outputrows;
	}
	
}
