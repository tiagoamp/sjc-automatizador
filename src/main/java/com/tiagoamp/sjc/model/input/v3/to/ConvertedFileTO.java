package com.tiagoamp.sjc.model.input.v3.to;

public class ConvertedFileTO {

	private String originalFileName;
	private String convertedFileName;
	private int operacionalRowsCount;
	private int administrativoRowsCount;
	
	
	public ConvertedFileTO() { }
	
	public ConvertedFileTO(String originalFileName, String convertedFileName, int operacionalRowsCount, int administrativoRowsCount) {
		this.originalFileName = originalFileName;
		this.convertedFileName = convertedFileName;
		this.operacionalRowsCount = operacionalRowsCount;
		this.administrativoRowsCount = administrativoRowsCount;
	}
	
	
	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public String getConvertedFileName() {
		return convertedFileName;
	}
	public void setConvertedFileName(String convertedFileName) {
		this.convertedFileName = convertedFileName;
	}
	public int getOperacionalRowsCount() {
		return operacionalRowsCount;
	}
	public void setOperacionalRowsCount(int operacionalRowsCount) {
		this.operacionalRowsCount = operacionalRowsCount;
	}
	public int getAdministrativoRowsCount() {
		return administrativoRowsCount;
	}
	public void setAdministrativoRowsCount(int administrativoRowsCount) {
		this.administrativoRowsCount = administrativoRowsCount;
	}
		
}
