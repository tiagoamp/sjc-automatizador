package com.tiagoamp.sjc.model.input;

import java.util.ArrayList;
import java.util.List;

public class ConvertedFilesTO {
	
	private String afastamentoFileName;
	private List<ConvertedFileTO> convertedFilesTO;
	
	
	public ConvertedFilesTO() { 
		this.convertedFilesTO = new ArrayList<>();
	}
	
	public ConvertedFilesTO(String afastamentoFileName, List<ConvertedFileTO> convertedFilesTO) {
		this.afastamentoFileName = afastamentoFileName;
		this.convertedFilesTO = convertedFilesTO;
	}
	
	
	public String getAfastamentoFileName() {
		return afastamentoFileName;
	}
	public void setAfastamentoFileName(String afastamentoFileName) {
		this.afastamentoFileName = afastamentoFileName;
	}
	public List<ConvertedFileTO> getConvertedFilesTO() {
		return convertedFilesTO;
	}
	public void setConvertedFilesTO(List<ConvertedFileTO> convertedFilesTO) {
		this.convertedFilesTO = convertedFilesTO;
	}		
	
}
