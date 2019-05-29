package com.tiagoamp.sjc;

import static com.tiagoamp.sjc.SjcAutoApplication.DIR_ENTRADA;
import static com.tiagoamp.sjc.SjcAutoApplication.DIR_SAIDA;
import static com.tiagoamp.sjc.model.input.AfastamentosExcelSpreadsheet.NEW_AFASTAMENTO_IDENTIFIED_FILE_NAME;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Produces;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.input.v3.ConvertedSpreadsheet;
import com.tiagoamp.sjc.model.input.v3.to.ConvertedFileTO;
import com.tiagoamp.sjc.model.input.v3.to.ProcessedFileTO;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;
import com.tiagoamp.sjc.service.FilesService;
import com.tiagoamp.sjc.service.SpreadsheetServices;

@CrossOrigin
@RestController
@RequestMapping("/sjc/v3/")
public class SjcNewController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SjcNewController.class);
	
	@Autowired
	private SpreadsheetServices sjcService;
	
	@Autowired
	private FilesService filesService;
	
	
	@RequestMapping(value = "upload2", method = RequestMethod.POST)
	public Response uploadFile(@RequestParam(value="inputfile", required=true) MultipartFile file) {
		try {
			String filename = file.getOriginalFilename();
			if (filename.endsWith("xlsx") || filename.endsWith("xls") ) {
				filename = NEW_AFASTAMENTO_IDENTIFIED_FILE_NAME + ".xlsx";
			}			
			file.transferTo(DIR_ENTRADA.resolve(filename).toFile());
		} catch (IllegalStateException | IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return Response.created(URI.create(file.getName())).build();
	}
		
	@RequestMapping(value = "convert/total", method = RequestMethod.GET)
	public String getNumberOfInputFiles() {
		long total = 0;
		try {
			total = filesService.getNumberOfExistingConvertedFiles(DIR_ENTRADA);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return String.valueOf(total);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.DELETE)
	public Response cleanWorkingDirectories() {
		try {
			filesService.cleanDirectories(DIR_ENTRADA, DIR_SAIDA);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return Response.ok().build();
	}
			
	@RequestMapping(value = "upload/afast", method = RequestMethod.DELETE)
	public Response deleteAfastamentoSpreadsheet() {
		filesService.deleteAfastamentoSpreadsheet(DIR_ENTRADA);
		return Response.ok().build();
	}

	@RequestMapping(value = "convert", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseEntity<List<ConvertedFileTO>> convertDataFromInputFiles() {
		try {
			List<ConvertedFileTO> tos = sjcService.convertInputFiles(DIR_ENTRADA);
			ResponseEntity<List<ConvertedFileTO>> entity = new ResponseEntity<>(tos, HttpStatus.CREATED);
			return entity;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
	}
	
	@RequestMapping(value = "process", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseEntity<List<ProcessedFileTO>> processInputFiles() {
		try {
			List<ProcessedFileTO> tos = sjcService.processFilesFrom(DIR_ENTRADA);
			ResponseEntity<List<ProcessedFileTO>> entity = new ResponseEntity<>(tos, HttpStatus.CREATED);
			return entity;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
	}
	
	@RequestMapping(value = "output2", method = RequestMethod.GET)
	@Produces( {"application/vnd.ms-excel"} )
	public ResponseEntity<InputStreamResource> generateNewOutputSpreadsheet() {
		try {
			List<ConvertedSpreadsheet> convSpreadsheets = sjcService.loadConvertedSpreadsheetsFromDirectory(DIR_ENTRADA);
			Optional<Path> histAfastamentoPath = filesService.findAfastamentoSpreadsheetPath(DIR_ENTRADA);
			Path afastamentoSpreadsheetFile = histAfastamentoPath.orElse(null);		
			OutputSpreadsheet spreadsheet = sjcService.generateOutputSpreadSheetFrom(convSpreadsheets, afastamentoSpreadsheetFile);
			LocalDate now = LocalDate.now();
			Path resultFile = DIR_SAIDA.resolve("Resultado_" + now.getDayOfMonth() + "_" + now.getMonthValue() + "_" + now.getYear() + ".xls");
			sjcService.generateOuputSpreadsheetFile(resultFile, spreadsheet);
			
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.ms-excel"));
		    headers.add("Access-Control-Allow-Origin", "*");
		    headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
		    headers.add("Access-Control-Allow-Headers", "Content-Type");
		    headers.add("Content-Disposition", "filename=" + resultFile.getFileName());
		    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		    headers.add("Pragma", "no-cache");
		    headers.add("Expires", "0");
		    
		    ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(new InputStreamResource(new FileInputStream(resultFile.toFile())), headers, HttpStatus.OK);
		    return response;			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
	}
		
	@RequestMapping(value = "messages", method = RequestMethod.GET)
	@Produces( {"application/pdf"} )
	public ResponseEntity<InputStreamResource> getProcessingMessagesFile() {
		try {
			List<ProcessedFileTO> tos = sjcService.processFilesFrom(DIR_ENTRADA);
			LocalDate now = LocalDate.now();
			Path resultFile = DIR_SAIDA.resolve("Mensagens_" + now.getDayOfMonth() + "_" + now.getMonthValue() + "_" + now.getYear() + ".pdf");
			sjcService.generateProcessingMessagesFile(resultFile, tos);
			
			HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/pdf"));
		    headers.add("Access-Control-Allow-Origin", "*");
		    headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
		    headers.add("Access-Control-Allow-Headers", "Content-Type");
		    headers.add("Content-Disposition", "filename=" + resultFile.getFileName());
		    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		    headers.add("Pragma", "no-cache");
		    headers.add("Expires", "0");
		    
		    ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(new InputStreamResource(new FileInputStream(resultFile.toFile())), headers, HttpStatus.OK);
		    return response;
		} catch (IOException | DocumentException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
	}
		
}
