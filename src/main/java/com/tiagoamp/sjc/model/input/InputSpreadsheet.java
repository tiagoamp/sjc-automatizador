package com.tiagoamp.sjc.model.input;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InputSpreadsheet {
	
	private String fileName;
	private String lotacao;
	private YearMonth yearMonthRef;
	private Map<SjcGeneralCode, InSheet> sheets;
	private List<ProcessingMessage> messages;
	
	
	public InputSpreadsheet() {
		messages = new ArrayList<>();
		sheets = new HashMap<>();
	}
	
	
	public List<ProcessingMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ProcessingMessage> messages) {
		this.messages = messages;
	}
	public Map<SjcGeneralCode, InSheet> getSheets() {
		return sheets;
	}
	public void setSheets(Map<SjcGeneralCode, InSheet> sheets) {
		this.sheets = sheets;
	}
	public String getLotacao() {
		return lotacao;
	}
	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public YearMonth getYearMonthRef() {
		return yearMonthRef;
	}
	public void setYearMonthRef(YearMonth yearMonthRef) {
		this.yearMonthRef = yearMonthRef;
	}
		
}
