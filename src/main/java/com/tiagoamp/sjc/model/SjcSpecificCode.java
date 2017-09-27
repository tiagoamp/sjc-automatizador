package com.tiagoamp.sjc.model;

public enum SjcSpecificCode {

	ADMINISTRATIVO_ADICIONALNOTURNO(1033, "Administrativo - Adicional Noturno", SjcGeneralCode.ADMINISTRATIVO, SjcItemType.ADICIONAL_NOTURNO),
	ADMINISTRATIVO_HORAEXTRA(1075,"Administrativo - Hora Extra", SjcGeneralCode.ADMINISTRATIVO, SjcItemType.HORA_EXTRA),
	OPERACIONAL_ADICIONALNOTURNO(1078,"Operacional - Adicional Noturno", SjcGeneralCode.OPERACIONAL, SjcItemType.ADICIONAL_NOTURNO),
	OPERACIONAL_HORAEXTRA(1035,"Operacional - Hora Extra", SjcGeneralCode.OPERACIONAL, SjcItemType.HORA_EXTRA),
	OPERACIONAL_PLANTOESEXTRA(1003,"Operacional - Plantao Extra", SjcGeneralCode.OPERACIONAL, SjcItemType.PLANTAO_EXTRA);
		
	
	private SjcSpecificCode(Integer code, String description, SjcGeneralCode generalCode, SjcItemType type) {
		this.setCode(code);
		this.setDescription(description);
		this.setGenericCode(generalCode);
		this.setType(type);
	}
		
	
	private Integer code;
	private String description;
	private SjcGeneralCode genericCode;
	private SjcItemType type;
	
			
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
	public SjcGeneralCode getGenericCode() {
		return genericCode;
	}
	public void setGenericCode(SjcGeneralCode genericCode) {
		this.genericCode = genericCode;
	}
	public SjcItemType getType() {
		return type;
	}
	public void setType(SjcItemType type) {
		this.type = type;
	}
	
}
