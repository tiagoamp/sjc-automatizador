package com.tiagoamp.sjc.model.input.v3.to;

import java.util.List;

public class IndexesPairTO {

	private Integer init;
	private Integer end;
	private List<Integer> dataIndexes;
	
	
	public IndexesPairTO() { }

	public IndexesPairTO(Integer init, Integer end, List<Integer> dataIndexes) {
		this.init = init;
		this.end = end;
		this.dataIndexes = dataIndexes;
	}

	
	public Integer getInit() {
		return init;
	}
	public void setInit(Integer init) {
		this.init = init;
	}
	public Integer getEnd() {
		return end;
	}
	public void setEnd(Integer end) {
		this.end = end;
	}
	public List<Integer> getDataIndexes() {
		return dataIndexes;
	}
	public void setDataIndexes(List<Integer> dataIndexes) {
		this.dataIndexes = dataIndexes;
	}
}

