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
		groupRowsByMatricula();		
	}
	
	
	private void sortRowsByLotacao() {
		rows.sort(Comparator.comparing(OutRow::getLotacao));		
	}
	
	private void groupRowsByMatricula() {
		List<OutRow> groupedRows = new ArrayList<>();
		
		rows.forEach( row -> {			
			List<OutRow> samePersonRows = rows.stream().filter(r -> r.getMatricula().equals(row.getMatricula()) && r.getLotacao() != row.getLotacao()).collect(Collectors.toList());
			boolean hasManyRows = samePersonRows.size() > 0;
			samePersonRows.add(0, row);			
			samePersonRows.forEach(filteredRow -> {
				filteredRow.setDuplicates(hasManyRows);
				if (!groupedRows.contains(filteredRow)) groupedRows.add(filteredRow);
			});			
		});
		
		rows = groupedRows;		
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
