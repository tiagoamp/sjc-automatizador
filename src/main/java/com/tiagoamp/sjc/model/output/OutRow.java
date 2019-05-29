package com.tiagoamp.sjc.model.output;

public class OutRow {
	
	private String lotacao;
	private String nome;
	private String matricula;
	private Integer quantidade;
	private String[] dtPlantoesExtras = new String[5];
	private String afastamento;	
	private Boolean[] dtPlantoesWithinAfastamentos = new Boolean[5];
	private boolean duplicates;
	
	
	public OutRow() { }
	
	public OutRow(String lotacao, String nome, String matricula) {
		this.lotacao = lotacao;
		this.nome = nome;
		this.matricula = matricula;
	}
	
	public OutRow(String lotacao, String nome, String matricula, Integer quantidade, String[] dtPlantoesEstras) {
		this(lotacao, nome, matricula);
		this.quantidade = quantidade;
		this.dtPlantoesExtras = dtPlantoesEstras;
	}
	
	
	@Override
	public String toString() {
		return String.format("%s | %s | %s", matricula, nome, lotacao);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof OutRow) ) return false;
		OutRow other = (OutRow) obj;
		return this.toString().equals(other.toString());
	}
	
	
	public String getLotacao() {
		return lotacao;
	}
	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nomeServidor) {
		this.nome = nomeServidor;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matriculaServidor) {
		this.matricula = matriculaServidor;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public String[] getDtPlantoesExtras() {
		return dtPlantoesExtras;
	}
	public void setDtPlantoesExtras(String[] dtPlantoesExtras) {
		this.dtPlantoesExtras = dtPlantoesExtras;
	}
	public String getAfastamento() {
		return afastamento;
	}
	public void setAfastamento(String afastamento) {
		this.afastamento = afastamento;
	}
	public Boolean[] getDtPlantoesWithinAfastamentos() {
		return dtPlantoesWithinAfastamentos;
	}
	public void setDtPlantoesWithinAfastamentos(Boolean[] dtPlantoesWithinAfastamentos) {
		this.dtPlantoesWithinAfastamentos = dtPlantoesWithinAfastamentos;
	}
	public void setDuplicates(boolean duplicates) {
		this.duplicates = duplicates;
	}
	public boolean hasDuplicates() {
		return this.duplicates;
	}

}
