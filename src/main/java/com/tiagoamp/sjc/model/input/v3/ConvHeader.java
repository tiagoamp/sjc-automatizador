package com.tiagoamp.sjc.model.input.v3;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class ConvHeader {
	
	private final String fixedTitle = "RELATÓRIO MENSAL DE HORA EXTRA, ADICIONAL NOTURNO E PLANTÃO EXTRA";
	private YearMonth yearMonthRef;
	
	private String nomeUnidadePrisional;
	private String monthRefAsStr;
	private String yearRefAsStr;
	
	
	public ConvHeader() { } 
			
	public ConvHeader(String nomeUnidadePrisional, String monthRefAsStr, String yearRefAsStr) {
		this.nomeUnidadePrisional = nomeUnidadePrisional;
		this.monthRefAsStr = monthRefAsStr;
		this.yearRefAsStr = yearRefAsStr;
	}

	public ConvHeader(YearMonth yearMonthRef, String nomeUnidadePrisional) {
		this.yearMonthRef = yearMonthRef;
		this.monthRefAsStr = yearMonthRef.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "br")).toUpperCase();
		this.yearRefAsStr = String.valueOf(yearMonthRef.getYear());
		this.nomeUnidadePrisional = nomeUnidadePrisional;
	}
	

	public YearMonth getYearMonthRef() {
		return yearMonthRef;
	}
	public void setYearMonthRef(YearMonth yearMonthRef) {
		this.yearMonthRef = yearMonthRef;
	}
	public String getNomeUnidadePrisional() {
		return nomeUnidadePrisional;
	}
	public void setNomeUnidadePrisional(String nomeUnidadePrisional) {
		this.nomeUnidadePrisional = nomeUnidadePrisional;
	}
	public String getMonthRefAsStr() {
		return monthRefAsStr;
	}
	public void setMonthRefAsStr(String monthRefAsStr) {
		this.monthRefAsStr = monthRefAsStr;
	}
	public String getYearRefAsStr() {
		return yearRefAsStr;
	}
	public void setYearRefAsStr(String yearRefAsStr) {
		this.yearRefAsStr = yearRefAsStr;
	}
	public String getFixedTitle() {
		return fixedTitle;
	}
			
}
