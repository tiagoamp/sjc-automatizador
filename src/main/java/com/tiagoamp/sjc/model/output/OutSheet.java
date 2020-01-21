package com.tiagoamp.sjc.model.output;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.tiagoamp.sjc.model.SjcSpecificCode;

public class OutSheet {
	
	private SjcSpecificCode code;
	private List<OutRow> rows;

	
	public OutSheet(SjcSpecificCode code) {
		this.code = code;
		rows = new ArrayList<>();
	}
	
	
	public void sortRows() {
		sortRowsByLotacao();
		groupRowsByMatricula();		
	}
	
	public void mergeRows() {
		sortRowsByLotacao();
		mergeRowsByMatricula();		
	}
		
	
	private void sortRowsByLotacao() {
		rows.sort(Comparator.comparing(OutRow::getLotacao));		
	}
	
	private void groupRowsByMatricula() {
		List<OutRow> groupedRows = new ArrayList<>();
		
		rows.forEach( row -> {			
			List<OutRow> samePersonRows = rows.stream()
					.filter(r -> r.getMatricula().equals(row.getMatricula()) && r.getLotacao() != row.getLotacao())
					.collect(Collectors.toList());
			boolean hasDuplicates = samePersonRows.size() > 0;
			samePersonRows.add(0, row);			
			samePersonRows.forEach(filteredRow -> {
				filteredRow.setDuplicates(hasDuplicates);
				if (hasDuplicates) filteredRow.setMessage("** Matrícula repete nesta planilha para outra lotação");
				if (!groupedRows.contains(filteredRow)) 
					groupedRows.add(filteredRow);
			});			
		});
		
		rows = groupedRows;		
	}
	
	private void mergeRowsByMatricula() {
		if (code != SjcSpecificCode.OPERACIONAL_PLANTOESEXTRA) 
			return;
		List<OutRow> mergedRows = new ArrayList<>();
		List<String> duplicatedMatriculas = new ArrayList<String>();
		
		rows.forEach( row -> {			
			List<OutRow> samePersonRows = rows.stream()
					.filter(r -> r.getMatricula().equals(row.getMatricula()) && r.getLotacao() != row.getLotacao())
					.collect(Collectors.toList());
			boolean hasDuplicates = samePersonRows.size() > 0;
			samePersonRows.add(0, row);
			
			if (!hasDuplicates) {
				samePersonRows.forEach(filteredRow -> {
					filteredRow.setDuplicates(hasDuplicates);				
					if (!mergedRows.contains(filteredRow)) 
						mergedRows.add(filteredRow);
				});
			} else {
				if (duplicatedMatriculas.contains(row.getMatricula()))
					return;
				OutRow mergedRow = new OutRow(row.getLotacao(), row.getNome(), row.getMatricula());
				
				String msg = "Agrupado plantões de : ";
				for (int i = 1; i < samePersonRows.size(); i++) {  // do not get index '0'
					OutRow r = samePersonRows.get(i);
					msg += "(+" + r.getQuantidade() + ") " + r.getLotacao() + "  ";
				}
				mergedRow.setMessage(msg);
				mergedRow.setAfastamento(row.getAfastamento());
				mergedRow.setDuplicates(hasDuplicates);
				int qtd = samePersonRows.stream().mapToInt(r -> r.getQuantidade()).sum();
				mergedRow.setQuantidade(qtd);
				String[] joinedDtPlantoesExtras = new String[5];
				List<String> listDtPlantoesExtras = samePersonRows.stream()
					.map(r -> r.getDtPlantoesExtras()).map(arr -> Arrays.asList(arr)).flatMap(l -> l.stream())
					.filter(x -> x!= null).filter(x -> !x.isEmpty()).collect(Collectors.toList());
				int endIndexExclusive = listDtPlantoesExtras.size() > 5 ? 5 : listDtPlantoesExtras.size();
				listDtPlantoesExtras.subList(0, endIndexExclusive).toArray(joinedDtPlantoesExtras);
				mergedRow.setDtPlantoesExtras(joinedDtPlantoesExtras);
				Boolean[] joinedDtPlantoesWithinAfastamentos = new Boolean[5];
				List<Boolean> listDtPlantoesWithinAfastamentos = samePersonRows.stream()
					.map(r -> r.getDtPlantoesWithinAfastamentos()).map(arr -> Arrays.asList(arr)).flatMap(l -> l.stream())
					.filter(x -> x!= null).collect(Collectors.toList());
				listDtPlantoesWithinAfastamentos.subList(0, listDtPlantoesWithinAfastamentos.size()).toArray(joinedDtPlantoesWithinAfastamentos);
				mergedRow.setDtPlantoesWithinAfastamentos(joinedDtPlantoesWithinAfastamentos);
				mergedRows.add(mergedRow);
				duplicatedMatriculas.add(mergedRow.getMatricula());
			}
		});
		
		rows = mergedRows;		
	}
	
	
	public SjcSpecificCode getCode() {
		return code;
	}
	public void setCode(SjcSpecificCode specificCode) {
		this.code = specificCode;
	}
	public List<OutRow> getRows() {
		return rows;
	}
	public void setRows(List<OutRow> rows) {
		this.rows = rows;
	}
	
}
