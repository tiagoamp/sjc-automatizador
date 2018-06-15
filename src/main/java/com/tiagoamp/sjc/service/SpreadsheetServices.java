package com.tiagoamp.sjc.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.input.InputExcelSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputExcelSpreadsheet;

@Service
public class SpreadsheetServices {
	
	/**
	 * Load data from a spreadsheet of a given file path.
	 * 
	 * @param filepath
	 * @return InputSpreadsheet
	 * @throws IOException
	 */
	public InputSpreadsheet loadInputSpreadsheet(Path filepath) throws IOException {
		if (Files.notExists(filepath)) throw new IllegalArgumentException("Arquivo inexistente!");
		InputExcelSpreadsheet excelSheet = new InputExcelSpreadsheet(filepath);
		return excelSheet.toInputSpreadsheet();
	}
	
	public List<InputSpreadsheet> loadInputSpreadsheetsFromDirectory(Path directory) throws IOException {
		if (Files.notExists(directory)) throw new IllegalArgumentException("Diretório inexistente!");
		List<InputSpreadsheet> inputList = new ArrayList<>();
		DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
		for (Path file : stream) {
			InputExcelSpreadsheet excelSheet = new InputExcelSpreadsheet(file);
			InputSpreadsheet spreadsheet = excelSheet.toInputSpreadsheet();
			inputList.add(spreadsheet);				
		}		
		return inputList;
	}
	
	public OutputSpreadsheet generateOutputSpreadSheet(List<InputSpreadsheet> spreadsheetsList) {
		return outSpreadsheetFactory.loadDataFromInputSpreadsheets(spreadsheetsList);
	}
	
	public void generateOuputSpreadsheetFile(Path outputFile, OutputSpreadsheet outputSpreadsheet) throws IOException {
		//if (templateFile == null) templateFile = Paths.get("resources","template_output.xlsx");		
		outputSpreadsheet.generateOuputSpreadsheetFile(outputFile);
	}
	
	public void generateOutputMessagesFile(Path outputFile, OutputSpreadsheet outputSpreadsheet) throws FileNotFoundException, DocumentException  {
		outputSpreadsheet.generateOutputMessageFile(outputFile);
	}
	
}
