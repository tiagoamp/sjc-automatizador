package com.tiagoamp.sjc.model.input;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InSheet {
	
	private SjcGeneralCode code;
	private YearMonth yearMonthRef;
	private List<InRow> rows;
	private List<ProcessingMessage> messages;
		
	
	public InSheet(SjcGeneralCode code) {
		this.code = code;
		this.messages = new ArrayList<>();
		this.rows = new ArrayList<>();
	}
	
	public InSheet(SjcGeneralCode code, YearMonth yearMonthRef) {
		this(code);
		this.yearMonthRef = yearMonthRef;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		return ((obj instanceof InSheet)) && (((InSheet)obj).getCode() == this.code) ;		
	}
	
	@Override
	public int hashCode() {
		return this.code.getCode();
	}
	
	@Override
	public String toString() {
		return String.format("Código: %s | Linhas: %d | Mensagens: %s", code, rows.size(), messages.size());		 
	}
	
	public void print() {
		System.out.println(this.toString());
		rows.forEach(System.out::println);				
	}
	
	
	public List<InRow> getRows() {
		return rows;
	}
	public void setRows(List<InRow> inputrows) {
		this.rows = inputrows;
	}
	public SjcGeneralCode getCode() {
		return code;
	}
	public void setCode(SjcGeneralCode code) {
		this.code = code;
	}
	public List<ProcessingMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ProcessingMessage> messages) {
		this.messages = messages;
	}
	public YearMonth getYearMonthRef() {
		return yearMonthRef;
	}
	public void setYearMonthRef(YearMonth yearMonthRef) {
		this.yearMonthRef = yearMonthRef;
	}
		
}
