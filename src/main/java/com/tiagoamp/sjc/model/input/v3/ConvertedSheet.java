package com.tiagoamp.sjc.model.input.v3;

import java.util.List;

import com.tiagoamp.sjc.model.SjcGeneralCode;

public class ConvertedSheet {
		
	private SjcGeneralCode code;
	private ConvHeader header;
	private List<ConvRow> rows;
	
	
	public ConvertedSheet() { }
	
	public ConvertedSheet(SjcGeneralCode code, ConvHeader header, List<ConvRow> rows) {
		this.code = code;
		this.header = header;
		this.rows = rows;
	}
	
	
	public void print() {
		System.out.println(this);
		rows.forEach(System.out::println);
	}
	
	
	@Override
	public String toString() {
		return header.getFixedTitle() + " | " + 
	           header.getYearMonthRef() + " [" + header.getYearRefAsStr() + " / " + header.getMonthRefAsStr()  + "]" + " | " + 
	           "Qtd rows: " + " [" + (rows != null ? rows.size() : 0) + "]";			   
	}
	
	
	public SjcGeneralCode getCode() {
		return code;
	}
	public void setCode(SjcGeneralCode code) {
		this.code = code;
	}
	public ConvHeader getHeader() {
		return header;
	}
	public void setHeader(ConvHeader header) {
		this.header = header;
	}
	public List<ConvRow> getRows() {
		return rows;
	}
	public void setRows(List<ConvRow> rows) {
		this.rows = rows;
	}
	
}
