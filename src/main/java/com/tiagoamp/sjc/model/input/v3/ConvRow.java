package com.tiagoamp.sjc.model.input.v3;

import java.util.Arrays;

public class ConvRow {
	
	private String nome;
	private String matricula;
	private String qtdHoraExtra;
	private String qtdAdicionalNoturno;
	private String qtdPlantoesExtra;
	private String[] dtPlantoesExtras = new String[5];  
	
	private boolean checkInfo = false;
	
	
	@Override
	public String toString() {
		return String.format("Matrícula: %s | Nome: %s | Hora Extra: %s | Adic. Noturno: %s | Qtd Plantões: %s | Dt Plantões: %s", 
				matricula, nome, qtdHoraExtra, qtdAdicionalNoturno, qtdPlantoesExtra, Arrays.toString(dtPlantoesExtras));
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
	public String getQtdAdicionalNoturno() {
		return qtdAdicionalNoturno;
	}
	public void setQtdAdicionalNoturno(String qtdAdicionalNoturno) {
		this.qtdAdicionalNoturno = qtdAdicionalNoturno;
	}
	public String getQtdHoraExtra() {
		return qtdHoraExtra;
	}
	public void setQtdHoraExtra(String qtdHoraExtra) {
		this.qtdHoraExtra = qtdHoraExtra;
	}
	public String getQtdPlantoesExtra() {
		if (qtdPlantoesExtra == null || qtdPlantoesExtra.isEmpty()) {
			int qtd = 0;
			for (int i = 0; i < dtPlantoesExtras.length; i++) {
				String dtPl = dtPlantoesExtras[i];
				if (dtPl != null && !dtPl.isEmpty() && dtPl.matches("^\\d+$"))
					qtd++;
			}
			qtdPlantoesExtra = String.valueOf(qtd);
		}		
		return qtdPlantoesExtra;
	}
	public void setQtdPlantoesExtra(String qtdPlantoesExtra) {
		this.qtdPlantoesExtra = qtdPlantoesExtra;
	}
	public String[] getDtPlantoesExtras() {
		return dtPlantoesExtras;
	}
	public void setDtPlantoesExtras(String[] dtPlantoesExtras) {
		this.dtPlantoesExtras = dtPlantoesExtras;
	}
	public void setCheckInfo(boolean checkInfo) {
		this.checkInfo = checkInfo;
	}
	public boolean shouldCkeckInfo() {
		return this.checkInfo;
	}
	
}
