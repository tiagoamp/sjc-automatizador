package com.tiagoamp.sjc.model.input;

public class LoadedFileTO {

	private String originalFileName;
	private String convertedFileName;
	private int operacionalNr;
	private int administrativoNr;
	
	
	public LoadedFileTO() { }
	
	public LoadedFileTO(String originalFileName, String loadedFileName, int operacionalNr, int administrativoNr) {
		this.originalFileName = originalFileName;
		this.convertedFileName = loadedFileName;
		this.operacionalNr = operacionalNr;
		this.administrativoNr = administrativoNr;
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
	public int getOperacionalNr() {
		return operacionalNr;
	}
	public void setOperacionalNr(int operacionalNr) {
		this.operacionalNr = operacionalNr;
	}
	public int getAdministrativoNr() {
		return administrativoNr;
	}
	public void setAdministrativoNr(int administrativoNr) {
		this.administrativoNr = administrativoNr;
	}	
		
}
