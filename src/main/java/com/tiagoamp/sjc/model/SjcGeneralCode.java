package com.tiagoamp.sjc.model;

public enum SjcGeneralCode {

	OPERACIONAL(0,"Operacional"),
	ADMINISTRATIVO(1,"Administrativo");
	
	
	private SjcGeneralCode(Integer code, String description) {
		this.code = code;
		this.description =  description;
	}
		
	
	private Integer code;
	private String description;
	
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer codigo) {
		this.code = codigo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String descricao) {
		this.description = descricao;
	}
	
}
