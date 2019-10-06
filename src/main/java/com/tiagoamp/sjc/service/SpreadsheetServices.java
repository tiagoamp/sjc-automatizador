package com.tiagoamp.sjc.service;

import static com.tiagoamp.sjc.model.input.AfastamentosExcelSpreadsheet.AFASTAMENTO_IDENTIFIED_FILE_NAME;
import static com.tiagoamp.sjc.model.input.AfastamentosExcelSpreadsheet.NEW_AFASTAMENTO_IDENTIFIED_FILE_NAME;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.dao.ExcelFileDao;
import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.input.AfastamentosExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.HistoricoAfastamentos;
import com.tiagoamp.sjc.model.input.InputExcelSpreadsheet;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.input.v3.ConvertedSpreadsheet;
import com.tiagoamp.sjc.model.input.v3.IInputConverter;
import com.tiagoamp.sjc.model.input.v3.InputConverterEadgyoLib;
import com.tiagoamp.sjc.model.input.v3.InputSpreadSheetProcessor;
import com.tiagoamp.sjc.model.input.v3.to.ConvertedFileTO;
import com.tiagoamp.sjc.model.input.v3.to.ProcessedFileTO;
import com.tiagoamp.sjc.model.output.OutputExcelSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputFilesGenerator;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;

@Service
public class SpreadsheetServices {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetServices.class);
	
	@Autowired
	private ExcelFileDao excelFileDao; 
	
	
	public List<ConvertedFileTO> convertInputFiles(Path dir) throws IOException {
		LOGGER.info("Convertendo arquivos do diretório...");
		if (Files.notExists(dir)) throw new IllegalArgumentException("Diretório inexistente!");
		List<ConvertedFileTO> tos = new ArrayList<>();		
		
		DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
		for (Path file : stream) {
			String filename = file.getFileName().toString();
			if (!filename.toUpperCase().endsWith("PDF")) continue;
			//InputConverter converter = new InputConverter(file);
			IInputConverter converter = new InputConverterEadgyoLib(file);
			ConvertedSpreadsheet convertedSpreadsheet = converter.convert();				
			String convFileName = convertedSpreadsheet.getOriginalFile().getFileName().toString().replaceAll("(.PDF|.pdf)$", ".xlsx");
			Path convSpreadsheetFile = excelFileDao.createConvertedSpreadsheet(convertedSpreadsheet, convFileName);
			convertedSpreadsheet.setConvertedFile(convSpreadsheetFile);				
			ConvertedFileTO convertedFileTO = convertedSpreadsheet.toConvertedFileTO();
			tos.add(convertedFileTO);					
		}
		
		return tos;
	}
	
	@Deprecated
	public List<InputSpreadsheet> loadInputSpreadsheetsFromDirectory(Path directory) throws IOException {
		LOGGER.info("Carregando arquivos do diretório...");
		if (Files.notExists(directory)) throw new IllegalArgumentException("Diretório inexistente!");
		List<InputSpreadsheet> inputList = new ArrayList<>();
		DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
		for (Path file : stream) {
			boolean isAfastamentoSpreasheetFile = file.getFileName().toString().toLowerCase().contains(AFASTAMENTO_IDENTIFIED_FILE_NAME); 
			if (isAfastamentoSpreasheetFile) continue;
			InputSpreadsheet spreadsheet = this.loadInputSpreadsheet(file);
			inputList.add(spreadsheet);				
		}		
		return inputList;
	}
	
	@Deprecated
	public InputSpreadsheet loadInputSpreadsheet(Path filepath) throws IOException {
		if (Files.notExists(filepath)) throw new IllegalArgumentException("Arquivo inexistente!");
		InputExcelSpreadsheet excelSheet = new InputExcelSpreadsheet(filepath);
		return excelSheet.loadFromFile();
	}
	
	public List<ProcessedFileTO> processFilesFrom(Path directory) throws IOException {
		boolean foundAfastamentoSheet = false;
		InputSpreadSheetProcessor processor = new InputSpreadSheetProcessor();
		List<ProcessedFileTO> result = new ArrayList<>();
		
		DirectoryStream<Path> stream = Files.newDirectoryStream(directory);				
		for (Path file : stream) {
			if (!file.getFileName().toString().endsWith(".xlsx")) continue;
			
			if (file.getFileName().toString().startsWith(NEW_AFASTAMENTO_IDENTIFIED_FILE_NAME)) {
				foundAfastamentoSheet = true;
				ProcessedFileTO to = new ProcessedFileTO(file.getFileName().toString(),	Arrays.asList(new ProcessingMessage(MessageType.INFO, "Identificada planilha de Afastamentos.")));
				result.add(to);
				continue;
			}
			
			boolean isValidLayout = excelFileDao.verifySpreadSheetLayout(file);
			if (!isValidLayout) {
				ProcessedFileTO to = new ProcessedFileTO(file.getFileName().toString(),	Arrays.asList(new ProcessingMessage(MessageType.ERROR, "Planilha com problema de Layout e será desconsiderada no processamento.")));
				result.add(to);
				continue;
			}
						
			ConvertedSpreadsheet spreadsheet = excelFileDao.loadFrom(file);
			List<ProcessingMessage> messages = processor.process(spreadsheet);
			if (messages.isEmpty()) continue;
			
			ProcessedFileTO to = new ProcessedFileTO(file.getFileName().toString(), messages);
			result.add(to);
		}
		
		if (!foundAfastamentoSheet) {
			ProcessedFileTO to = new ProcessedFileTO("Planilha de Afastamentos", Arrays.asList(new ProcessingMessage(MessageType.INFO, "Não foi identificada planilha de afastamentos nos arquivos.")));
			result.add(to);
		}
		
		return result;
	}
	
	public List<ConvertedSpreadsheet> loadConvertedSpreadsheetsFromDirectory(Path dir) throws FileNotFoundException, IOException {
		InputSpreadSheetProcessor processor = new InputSpreadSheetProcessor();
		List<ConvertedSpreadsheet> result = new ArrayList<>();		
		DirectoryStream<Path> stream = Files.newDirectoryStream(dir);				
		for (Path file : stream) {
			if (!file.getFileName().toString().endsWith(".xlsx")) continue;			
			if (file.getFileName().toString().startsWith(NEW_AFASTAMENTO_IDENTIFIED_FILE_NAME)) continue;
			boolean isValidLayout = excelFileDao.verifySpreadSheetLayout(file);
			if (!isValidLayout) continue;
			ConvertedSpreadsheet spreadsheet = excelFileDao.loadFrom(file);
			processor.process(spreadsheet);
			result.add(spreadsheet);
		}
		return result;
	}
	
	public HistoricoAfastamentos loadAfastamentosSpreadsheet(Path filepath) throws IOException {
		LOGGER.info("Carregando planilha de afastamentos...");
		if (Files.notExists(filepath)) throw new IllegalArgumentException("Arquivo inexistente!");
		AfastamentosExcelSpreadsheet spreadsheet = new AfastamentosExcelSpreadsheet(filepath);
		return spreadsheet.loadFromFile();
	}
	
	@Deprecated
	public OutputSpreadsheet generateOutputSpreadSheet(List<InputSpreadsheet> inputSpreadsheets, Path histAfastamentoFilePath) throws IOException {
		LOGGER.info("Gerando planilha de saída...");
		HistoricoAfastamentos afastamentos = null;
		if (histAfastamentoFilePath != null && Files.exists(histAfastamentoFilePath)) afastamentos = loadAfastamentosSpreadsheet(histAfastamentoFilePath);
		OutputExcelSpreadsheet excelSheet = new OutputExcelSpreadsheet();		
		return excelSheet.loadDataFromInputSpreadsheets(inputSpreadsheets, afastamentos);
	}
	
	public OutputSpreadsheet generateOutputSpreadSheetFrom(List<ConvertedSpreadsheet> convSpreadsheets, Path histAfastamentoFilePath) throws IOException {
		LOGGER.info("Gerando planilha de saída...");
		HistoricoAfastamentos afastamentos = null;
		if (histAfastamentoFilePath != null && Files.exists(histAfastamentoFilePath)) afastamentos = loadAfastamentosSpreadsheet(histAfastamentoFilePath);
		OutputExcelSpreadsheet excelSheet = new OutputExcelSpreadsheet();		
		return excelSheet.loadDataFromConvertedSpreadsheets(convSpreadsheets, afastamentos);
	}
	
	public void generateOuputSpreadsheetFile(Path outputFile, OutputSpreadsheet spreadsheet) throws IOException {
		LOGGER.info("Gerando arquivo da planilha de saída...");
		OutputFilesGenerator filesGenerator = new OutputFilesGenerator();
		filesGenerator.generateOuputSpreadsheetFile(outputFile, spreadsheet);
		LOGGER.info("Arquivo gerado!");
	}
	
	public void generateOutputMessagesFile(Path outputFile, OutputSpreadsheet spreadsheet) throws FileNotFoundException, DocumentException  {
		LOGGER.info("Gerando arquivo de mensagens...");
		OutputFilesGenerator filesGenerator = new OutputFilesGenerator();
		filesGenerator.generateOutputMessageFile(outputFile, spreadsheet);
	}
	
	public void generateProcessingMessagesFile(Path outputFile, List<ProcessedFileTO> processedTOs) throws FileNotFoundException, DocumentException  {
		LOGGER.info("Gerando arquivo de mensagens...");		
		Map<String, List<ProcessingMessage>> msgsMap = new HashMap<>();		
		processedTOs.forEach(to -> msgsMap.put(to.getFileName().toString(), to.getMessages())  );		
		PDFGenerator pdfGen = new PDFGenerator();
		pdfGen.generateMessagesPdfFile(msgsMap, outputFile);
	}
	
}
