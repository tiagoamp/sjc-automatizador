package com.tiagoamp.sjc.model;

import java.util.Comparator;

import com.tiagoamp.sjc.model.output.OutRow;

public class LotacaoComparator implements Comparator<OutRow> {

	@Override
	public int compare(OutRow r1, OutRow r2) {
		return r1.getLotacao().compareTo(r2.getLotacao());
	}		

}
