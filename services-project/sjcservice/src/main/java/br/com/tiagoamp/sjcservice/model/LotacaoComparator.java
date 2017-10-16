package br.com.tiagoamp.sjcservice.model;

import java.util.Comparator;

import br.com.tiagoamp.sjcservice.model.output.OutRow;

public class LotacaoComparator implements Comparator<OutRow> {

	@Override
	public int compare(OutRow r1, OutRow r2) {
		return r1.getLotacao().compareTo(r2.getLotacao());
	}		

}
