package com.tiagoamp.sjc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.input.LoadedFilesTO;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;
import com.tiagoamp.sjc.service.SpreadsheetServices;
import com.tiagoamp.sjc.service.FilesService;
import static com.tiagoamp.sjc.SjcAutoApplication.*;

@CrossOrigin
@RestController
@RequestMapping("/sjc")
public class SjcController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SjcController.class);
	
	@Autowired
	private SpreadsheetServices sjcService;
	
	@Autowired
	private FilesService filesService;
	
		
	@Deprecated
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public Response uploadFile(MultipartHttpServletRequest request) {
		try {
			Iterator<String> itr = request.getFileNames();
			while (itr.hasNext()) {
				String uploadedFile = itr.next();
				MultipartFile mfile = request.getFile(uploadedFile);
				String filepathStr = DIR_ENTRADA.toString() + File.separator +  mfile.getOriginalFilename();
				filesService.saveMultipartFileInFileSystem(mfile, filepathStr);								
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());			
			return Response.serverError().build();
		}
		return Response.ok().build();
	}
	
	@RequestMapping(value = "upload2", method = RequestMethod.POST)
	public Response uploadFile(@RequestParam(value="inputfile", required=true) MultipartFile file) {
		try {
			file.transferTo(DIR_ENTRADA.resolve(file.getOriginalFilename()).toFile());
		} catch (IllegalStateException | IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return Response.created(URI.create(file.getName())).build();
	}
	
	@RequestMapping(value = "upload/total", method = RequestMethod.GET)
	public String getNumberOfUploadedSpreadsheets() {
		long total = 0;
		try {
			total = filesService.getNumberOfFilesInUploadDirectory(DIR_ENTRADA);
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
	
	@Deprecated
	@RequestMapping(value = "upload", method = RequestMethod.DELETE)
	public Response cleanUploadDirectory() {
		return cleanWorkingDirectories();
	}
	
	@RequestMapping(value = "upload/afast", method = RequestMethod.DELETE)
	public Response deleteAfastamentoSpreadsheet() {
		filesService.deleteAfastamentoSpreadsheet(DIR_ENTRADA);
		return Response.ok().build();
	}
	
	@Deprecated
	@RequestMapping(value = "input", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public InputSpreadsheet getSheetAtIndex(@QueryParam(value = "index") String index) {
		InputSpreadsheet insheet = null;
		try {
			List<Path> list = filesService.getUploadedFilesPath(DIR_ENTRADA);
			Path filepath = list.get(Integer.valueOf(index));
			insheet = sjcService.loadInputSpreadsheet(filepath);			
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
		return insheet;
	}
	
	@RequestMapping(value = "load", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public ResponseEntity<LoadedFilesTO> loadDataFromInputFiles() {
		try {
			sjcService.loadDataFromInputFiles(DIR_ENTRADA);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "output", method = RequestMethod.GET)
	@Produces( {"application/vnd.ms-excel"} )
	public ResponseEntity<InputStreamResource> generateOutputSpreadsheet() {
		try {
			List<InputSpreadsheet> list = sjcService.loadInputSpreadsheetsFromDirectory(DIR_ENTRADA);
			Optional<Path> histAfastamentoPath = filesService.findAfastamentoSpreadsheetPath(DIR_ENTRADA);
			Path afastamentoSpreadsheetFile = histAfastamentoPath.orElse(null);		
			
			OutputSpreadsheet spreadsheet = sjcService.generateOutputSpreadSheet(list, afastamentoSpreadsheetFile);
			
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
	
	@RequestMapping(value = "output/messages", method = RequestMethod.GET)
	@Produces( {"application/pdf"} )
	public ResponseEntity<InputStreamResource> getOutputMessagesFile() {
		OutputSpreadsheet outsheet = null;
		try {
			List<InputSpreadsheet> list = sjcService.loadInputSpreadsheetsFromDirectory(DIR_ENTRADA);
			Optional<Path> histAfastamentoPath = filesService.findAfastamentoSpreadsheetPath(DIR_ENTRADA);
			Path afastamentoSpreadsheetFile = histAfastamentoPath.orElse(null);		
						
			outsheet = sjcService.generateOutputSpreadSheet(list, afastamentoSpreadsheetFile);
			
			LocalDate now = LocalDate.now();
			Path resultFile = DIR_SAIDA.resolve("Mensagens_" + now.getDayOfMonth() + "_" + now.getMonthValue() + "_" + now.getYear() + ".pdf");
			sjcService.generateOutputMessagesFile(resultFile, outsheet);
			
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
