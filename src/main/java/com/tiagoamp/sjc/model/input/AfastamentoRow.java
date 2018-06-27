package com.tiagoamp.sjc.model.input;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AfastamentoRow {
	
	private LocalDate dataInicial;
	private LocalDate dataFinal;
	private LocalDate dataPrevistaRetorno;
	private String periodo;
	private String matricula;
	private String nome;
	private String motivo;
	private String tipo;
	
	
	public String getAfastamentoForOuputRow() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");			
		String dataFinalStr = dataFinal != null ? formatter.format(dataFinal) : "-";
		String periodoStr = periodo != null ? periodo : "-";
		return String.format("Afastamento de %s até %s (%s) | Matrícula = %s - %s", formatter.format(dataInicial), dataFinalStr, periodoStr, matricula, nome);
	}
	
	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");			
		String dataFinalStr = dataFinal != null ? formatter.format(dataFinal) : "-";
		String periodoStr = periodo != null ? periodo : "-";
		return String.format("From %s to %s (%s) | %s | %s", formatter.format(dataInicial), dataFinalStr, periodoStr, matricula, nome);
	}
	

	public LocalDate getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(LocalDate dataInicial) {
		this.dataInicial = dataInicial;
	}
	public LocalDate getDataFinal() {
		return dataFinal;
	}
	public void setDataFinal(LocalDate dataFinal) {
		this.dataFinal = dataFinal;
	}
	public LocalDate getDataPrevistaRetorno() {
		return dataPrevistaRetorno;
	}
	public void setDataPrevistaRetorno(LocalDate dataPrevistaRetorno) {
		this.dataPrevistaRetorno = dataPrevistaRetorno;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
