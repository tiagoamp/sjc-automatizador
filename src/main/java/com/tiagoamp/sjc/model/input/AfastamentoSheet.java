package com.tiagoamp.sjc.model.input;

import java.util.ArrayList;
import java.util.List;

public class AfastamentoSheet {
	
	private List<AfastamentoRow> rows;
	
	
	public AfastamentoSheet() {
		rows = new ArrayList<AfastamentoRow>();
	}
	
		
	public List<AfastamentoRow> getRows() {
		return rows;
	}
	public void setRows(List<AfastamentoRow> rows) {
		this.rows = rows;
	}
		
}
