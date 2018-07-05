package com.tiagoamp.sjc.model.output;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcSpecificCode;

public class OutputSpreadsheet {
	
	private Map<SjcSpecificCode, OutSheet> sheets;
	private Map<String,List<ProcessingMessage>> messages;
	
	
	public OutputSpreadsheet() {
		sheets = new HashMap<>();
		messages = new HashMap<>();
	}
	
			
	public Map<SjcSpecificCode, OutSheet> getSheets() {
		return sheets;
	}
	public void setSheets(Map<SjcSpecificCode, OutSheet> sheets) {
		this.sheets = sheets;
	}
	public Map<String, List<ProcessingMessage>> getMessages() {
		return messages;
	}

	
}
