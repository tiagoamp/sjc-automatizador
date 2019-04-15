package com.tiagoamp.sjc.model.input.v3;

import java.nio.file.Path;

public class ConvertedSpreadsheet {

	private ConvHeader header;	
	private ConvertedSheet operacionalSheet;
	private ConvertedSheet administrativoSheet;
	private Path originalFile;
	private Path convertedFile;
	
	
	public ConvertedSpreadsheet() { }
	
	public ConvertedSpreadsheet(ConvHeader header, ConvertedSheet operacionalSheet, ConvertedSheet administrativoSheet, Path originalFilePath) {
		if (header == null) {
			header = new ConvHeader();
			
			if (operacionalSheet != null && operacionalSheet.getHeader().getNomeUnidadePrisional() != null) {
				header.setNomeUnidadePrisional(operacionalSheet.getHeader().getNomeUnidadePrisional());
			} else if (administrativoSheet != null && administrativoSheet.getHeader().getNomeUnidadePrisional() != null) {
				header.setNomeUnidadePrisional(administrativoSheet.getHeader().getNomeUnidadePrisional());
			}
			
			if (operacionalSheet != null && operacionalSheet.getHeader().getYearMonthRef() != null) {
				header.setYearMonthRef(operacionalSheet.getHeader().getYearMonthRef());
			} else if (administrativoSheet != null && administrativoSheet.getHeader().getYearMonthRef() != null) {
				header.setYearMonthRef(administrativoSheet.getHeader().getYearMonthRef());
			}
			
			if (operacionalSheet != null && operacionalSheet.getHeader().getYearRefAsStr() != null) {
				header.setYearRefAsStr(operacionalSheet.getHeader().getYearRefAsStr());
			} else if (administrativoSheet != null && administrativoSheet.getHeader().getYearRefAsStr() != null) {
				header.setYearRefAsStr(administrativoSheet.getHeader().getYearRefAsStr());
			}
			
			if (operacionalSheet != null && operacionalSheet.getHeader().getMonthRefAsStr() != null) {
				header.setMonthRefAsStr(operacionalSheet.getHeader().getMonthRefAsStr());
			} else if (administrativoSheet != null && administrativoSheet.getHeader().getMonthRefAsStr() != null) {
				header.setMonthRefAsStr(administrativoSheet.getHeader().getMonthRefAsStr());
			}
		}
		
		this.header = header;
		this.operacionalSheet = operacionalSheet;
		this.administrativoSheet = administrativoSheet;
		this.originalFile = originalFilePath;
	}
	
	public ConvertedSpreadsheet(ConvertedSheet operacionalSheet, ConvertedSheet administrativoSheet, Path originalFilePath) {
		this(null, operacionalSheet, administrativoSheet, originalFilePath);		
	}
	
	
	public ConvHeader getHeader() {
		return header;
	}
	public void setHeader(ConvHeader header) {
		this.header = header;
	}
	public ConvertedSheet getOperacionalSheet() {
		return operacionalSheet;
	}
	public void setOperacionalSheet(ConvertedSheet operacionalSheet) {
		this.operacionalSheet = operacionalSheet;
	}
	public ConvertedSheet getAdministrativoSheet() {
		return administrativoSheet;
	}
	public void setAdministrativoSheet(ConvertedSheet administrativoSheet) {
		this.administrativoSheet = administrativoSheet;
	}
	public Path getConvertedFile() {
		return convertedFile;
	}
	public void setConvertedFile(Path convertedFile) {
		this.convertedFile = convertedFile;
	}
	public Path getOriginalFile() {
		return originalFile;
	}
	public void setOriginalFile(Path originalFile) {
		this.originalFile = originalFile;
	}
	
}
