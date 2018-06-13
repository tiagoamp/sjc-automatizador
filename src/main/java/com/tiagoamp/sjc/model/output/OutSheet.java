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
