package br.com.tiagoamp.sjcservice.model.output;

import java.util.ArrayList;
import java.util.List;

import br.com.tiagoamp.sjcservice.model.SjcSpecificCode;

public class OutSheet {
	
	public OutSheet(SjcSpecificCode code) {
		this.code = code;
		outputrows = new ArrayList<>();
	}
	
	private SjcSpecificCode code;
	private List<OutRow> outputrows;

	
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
