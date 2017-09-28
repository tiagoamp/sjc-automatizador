package com.tiagoamp.sjc.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;

@Service
public class SjcServicesFacade {

	/**
	 * Load data from a spreadsheet of a given file path.
	 * 
	 * @param filepath
	 * @return InputSpreadsheet
	 * @throws IOException
	 */
	public InputSpreadsheet loadInputSpreadsheet(Path filepath) throws IOException {
		if (Files.notExists(filepath)) throw new IllegalArgumentException("Diretório inexistente!");
		InputSpreadsheet spreadsheet = new InputSpreadsheet();
		spreadsheet.loadFromFile(filepath);	
		return spreadsheet;
	}
	
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
	
	@Deprecated
	public void generateOutputMessagesFile(Path outputFile, OutputSpreadsheet outputSpreadsheet) throws IOException {
		outputSpreadsheet.generateOutputMessagesPage(outputFile);
	}
	
}
