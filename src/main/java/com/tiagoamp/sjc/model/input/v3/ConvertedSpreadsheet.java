package com.tiagoamp.sjc.model.input.v3;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import static com.tiagoamp.sjc.model.SjcGeneralCode.*;
import com.tiagoamp.sjc.model.input.ConvertedFileTO;

public class ConvertedSpreadsheet {

	private ConvHeader header;	
	private Path originalFile;
	private Path convertedFile;
	private Map<SjcGeneralCode, ConvertedSheet> convertedSheets;
	
	
	public ConvertedSpreadsheet() {
		this.convertedSheets = new HashMap<>();
	}
	
	public ConvertedSpreadsheet(ConvHeader header, ConvertedSheet operacionalSheet, ConvertedSheet administrativoSheet, Path originalFilePath) {
		this();
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
		this.originalFile = originalFilePath;
		this.convertedSheets.put(OPERACIONAL, operacionalSheet);
		this.convertedSheets.put(ADMINISTRATIVO, administrativoSheet);		
	}
	
	public ConvertedSpreadsheet(ConvertedSheet operacionalSheet, ConvertedSheet administrativoSheet, Path originalFilePath) {
		this(null, operacionalSheet, administrativoSheet, originalFilePath);		
	}
	
	
	public ConvertedFileTO toConvertedFileTO() {
		String originalFileName = this.getOriginalFile().getFileName().toString();
		String convertedFileName = this.getConvertedFile().getFileName().toString();
		int opRowsCount = 0, admRowsCount = 0;
		ConvertedSheet opSheet = this.getConvertedSheets().get(OPERACIONAL);
		ConvertedSheet admSheet = this.getConvertedSheets().get(ADMINISTRATIVO);
		if (opSheet != null && opSheet.getRows() != null) opRowsCount = opSheet.getRows().size();
		if (admSheet != null && admSheet.getRows() != null) admRowsCount = admSheet.getRows().size();		
		return new ConvertedFileTO(originalFileName, convertedFileName, opRowsCount, admRowsCount);
	}
	
	
	public ConvHeader getHeader() {
		return header;
	}
	public void setHeader(ConvHeader header) {
		this.header = header;
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
	public Map<SjcGeneralCode, ConvertedSheet> getConvertedSheets() {
		return convertedSheets;
	}
	public void setConvertedSheets(Map<SjcGeneralCode, ConvertedSheet> convertedSheets) {
		this.convertedSheets = convertedSheets;
	}
	
}
