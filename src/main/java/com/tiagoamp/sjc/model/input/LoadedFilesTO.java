package com.tiagoamp.sjc.model.input;

import java.util.List;

public class LoadedFilesTO {
	
	private String afastamentoFileName;
	private List<LoadedFileTO> loadedFiles;
	
	
	public LoadedFilesTO() { }
	
	public LoadedFilesTO(String afastamentoFileName, List<LoadedFileTO> loadedFiles) {
		this.afastamentoFileName = afastamentoFileName;
		this.loadedFiles = loadedFiles;
	}
	
	
	public String getAfastamentoFileName() {
		return afastamentoFileName;
	}
	public void setAfastamentoFileName(String afastamentoFileName) {
		this.afastamentoFileName = afastamentoFileName;
	}
	public List<LoadedFileTO> getLoadedFiles() {
		return loadedFiles;
	}
	public void setLoadedFiles(List<LoadedFileTO> loadedFiles) {
		this.loadedFiles = loadedFiles;
	}	
	
}
