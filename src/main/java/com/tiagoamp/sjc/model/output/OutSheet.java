package com.tiagoamp.sjc.model.output;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.tiagoamp.sjc.model.SjcSpecificCode;

public class OutSheet {
	
	private SjcSpecificCode code;
	private List<OutRow> rows;

	
	public OutSheet(SjcSpecificCode code) {
		this.code = code;
		rows = new ArrayList<>();
	}
	
	
	public void sortRows() {
		sortRowsByLotacao();
		//groupRowsByMatricula();		
	}
	
	
	private void sortRowsByLotacao() {
		rows.sort(Comparator.comparing(OutRow::getLotacao));		
	}
	
	private void groupRowsByMatricula() {
		List<OutRow> groupedRows = new ArrayList<>();
		
		rows.forEach( row -> {			
			List<OutRow> filteredRows = rows.stream().filter(r -> r.getMatricula() == row.getMatricula()).collect(Collectors.toList());
			filteredRows.forEach(fRow -> {
				if (!groupedRows.contains(fRow)) groupedRows.add(fRow);
			});			
		});
		
		rows = groupedRows;
		
		
		/*List<OutRow> groupedRows = new ArrayList<>(rows); // creates new List from existing output rows
			
		for (int i = 0; i < rows.size(); i++) {
			OutRow currRow = rows.get(i);
			
			for (int j = i+1; j < rows.size(); j++) {
				OutRow nextRow = rows.get(j);
				
				if (currRow.getMatricula().equals(nextRow.getMatricula())) {
					groupedRows.remove(j);
					groupedRows.add(i+1, nextRow);
					i++;  // to not process inserted repeated element
				}
			}			
		}			
		rows = groupedRows;*/
	}
	
	
	public SjcSpecificCode getCode() {
		return code;
	}
	public void setCode(SjcSpecificCode specificCode) {
		this.code = specificCode;
	}
	public List<OutRow> getRows() {
		return rows;
	}
	public void setRows(List<OutRow> rows) {
		this.rows = rows;
	}
	
}
