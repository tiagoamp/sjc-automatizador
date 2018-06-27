package com.tiagoamp.sjc.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.input.AfastamentosExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.HistoricoAfastamentos;
import com.tiagoamp.sjc.model.input.InputExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputExcelSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputFilesGenerator;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;

@Service
public class SpreadsheetServices {
	
		public List<InputSpreadsheet> loadInputSpreadsheetsFromDirectory(Path directory) throws IOException {
		if (Files.notExists(directory)) throw new IllegalArgumentException("Diretório inexistente!");
		List<InputSpreadsheet> inputList = new ArrayList<>();
		DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
		for (Path file : stream) {
			InputSpreadsheet spreadsheet = this.loadInputSpreadsheet(file);
			inputList.add(spreadsheet);				
		}		
		return inputList;
	}
	
	public InputSpreadsheet loadInputSpreadsheet(Path filepath) throws IOException {
		if (Files.notExists(filepath)) throw new IllegalArgumentException("Arquivo inexistente!");
		InputExcelSpreadsheet excelSheet = new InputExcelSpreadsheet(filepath);
		return excelSheet.loadFromFile();
	}
	
	public HistoricoAfastamentos loadAfastamentosSpreadsheet(Path filepath) throws IOException {
		if (Files.notExists(filepath)) throw new IllegalArgumentException("Arquivo inexistente!");
		AfastamentosExcelSpreadsheet spreadsheet = new AfastamentosExcelSpreadsheet(filepath);
		return spreadsheet.loadFromFile();
	}
	
	public OutputSpreadsheet generateOutputSpreadSheet(List<InputSpreadsheet> inputSpreadSheets, Path histAfastamentoFilePath) throws IOException {
		HistoricoAfastamentos afastamentos = null;
		if (histAfastamentoFilePath != null && Files.exists(histAfastamentoFilePath)) afastamentos = loadAfastamentosSpreadsheet(histAfastamentoFilePath);
		OutputExcelSpreadsheet excelSheet = new OutputExcelSpreadsheet();		
		return excelSheet.loadDataFromInputSpreadsheets(inputSpreadSheets, afastamentos);
	}
	
	public void generateOuputSpreadsheetFile(Path outputFile, OutputSpreadsheet spreadsheet) throws IOException {
		OutputFilesGenerator filesGenerator = new OutputFilesGenerator();
		filesGenerator.generateOuputSpreadsheetFile(outputFile, spreadsheet);
	}
	
	public void generateOutputMessagesFile(Path outputFile, OutputSpreadsheet spreadsheet) throws FileNotFoundException, DocumentException  {
		OutputFilesGenerator filesGenerator = new OutputFilesGenerator();
		filesGenerator.generateOutputMessageFile(outputFile, spreadsheet);
	}
	
}
