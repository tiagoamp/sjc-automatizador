package com.tiagoamp.sjc.model.input;

public class InRow {
	
	private String nome;
	private String matricula;
	private Integer qtdHoraExtra;
	private Integer qtdAdicionalNoturno;
	private Integer qtdPlantoesExtra;
	
	
	@Override
	public String toString() {
		return String.format("Matrícula: %s | Nome: %s | Hora Extra: %s | Adic. Noturno: %s | Plantões: %s", 
				matricula, nome, qtdHoraExtra, qtdAdicionalNoturno, qtdPlantoesExtra);
	}
	
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public Integer getQtdHoraExtra() {
		return qtdHoraExtra;
	}
	public void setQtdHoraExtra(Integer qtdHoraExtra) {
		this.qtdHoraExtra = qtdHoraExtra;
	}
	public Integer getQtdAdicionalNoturno() {
		return qtdAdicionalNoturno;
	}
	public void setQtdAdicionalNoturno(Integer qtdAdicionalNoturno) {
		this.qtdAdicionalNoturno = qtdAdicionalNoturno;
	}
	public Integer getQtdPlantoesExtra() {
		return qtdPlantoesExtra;
	}
	public void setQtdPlantoesExtra(Integer qtdPlantoesExtra) {
		this.qtdPlantoesExtra = qtdPlantoesExtra;
	}	
	
}
