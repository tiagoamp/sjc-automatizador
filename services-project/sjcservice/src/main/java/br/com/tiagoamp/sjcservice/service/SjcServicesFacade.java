package br.com.tiagoamp.sjcservice.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import br.com.tiagoamp.sjcservice.model.input.InputSpreadsheet;
import br.com.tiagoamp.sjcservice.model.output.OutputSpreadsheet;

public class SjcServicesFacade {
	
	public List<InputSpreadsheet> loadInputSpreadsheetsFromDirectory(Path directory) throws IOException {
		if (Files.notExists(directory)) throw new IllegalArgumentException("Diretório inexistente!");
		List<InputSpreadsheet> inputList = new ArrayList<>();
		DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
		for (Path file : stream) {
			InputSpreadsheet spreadsheet = new InputSpreadsheet(); 
			spreadsheet.loadFromFile(file);	
			inputList.add(spreadsheet);				
		}		
		return inputList;
	}
	
	public OutputSpreadsheet generateOutputSpreadSheet(List<InputSpreadsheet> list) {
		OutputSpreadsheet outputSpreadSheet = new OutputSpreadsheet();
		outputSpreadSheet.loadDataFromInputSpreadSheets(list);
		return outputSpreadSheet;
	}
	
	public void generateOuputSpreadsheetFile(Path outputFile, OutputSpreadsheet outputSpreadsheet) throws IOException {
		outputSpreadsheet.generateOuputSpreadsheetFile(outputFile);
	}
	
	public void generateOutputMessagesFile(Path outputFile, OutputSpreadsheet outputSpreadsheet) throws IOException {
		outputSpreadsheet.generateOutputMessagesPage(outputFile);
	}

}
