package com.tiagoamp.sjc.model.output;

public class OutRow {
	
	public OutRow() {		
	}
	
	public OutRow(String lotacao, String nome, String matricula) {
		this.lotacao = lotacao;
		this.nome = nome;
		this.matricula = matricula;
	}
	
	public OutRow(String lotacao, String nome, String matricula, Integer quantidade) {
		this(lotacao, nome, matricula);
		this.quantidade = quantidade;
	}
	
	
	private String lotacao;
	private String nome;
	private String matricula;
	private Integer quantidade;
	
	
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

}
