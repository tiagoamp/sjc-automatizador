package com.tiagoamp.sjc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.itextpdf.text.DocumentException;
import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;
import com.tiagoamp.sjc.service.SpreadsheetServices;
import com.tiagoamp.sjc.service.UploadService;

@CrossOrigin
@RestController
@RequestMapping("/sjc")
public class SjcController {
	
	@Autowired
	private SpreadsheetServices sjcService;
	
	@Autowired
	private UploadService uploadService;
	
	
	private final Path UPLOAD_DIR = SjcAutoApplication.BASE_DIR.resolve("upload/");
	private final Path RESULT_DIR = SjcAutoApplication.BASE_DIR.resolve("resultado/");
	private final Path RESOURCES_DIR = SjcAutoApplication.BASE_DIR.resolve("resources/");
	
		
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public Response uploadFile(MultipartHttpServletRequest request) {
		try {
			Iterator<String> itr = request.getFileNames();
			while (itr.hasNext()) {
				String uploadedFile = itr.next();
				MultipartFile mfile = request.getFile(uploadedFile);
				String filepathStr = UPLOAD_DIR.toString() + File.separator +  mfile.getOriginalFilename();
				uploadService.saveMultipartFileInFileSystem(mfile, filepathStr);								
			}
		} catch (IOException e) {
			e.printStackTrace();			
			return Response.serverError().build();
		}
		return Response.ok().build();
	}
	
	@RequestMapping(value = "upload/total", method = RequestMethod.GET)
	public String getNumberOfUploadedSpreadsheets() {
		long total = 0;
		try {
			total = uploadService.getNumberOfFilesInUploadDirectory(UPLOAD_DIR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return String.valueOf(total);
	}
	
	@RequestMapping(value = "upload", method = RequestMethod.DELETE)
	public Response cleanUploadDirectory() {
		try {
			uploadService.cleanDirectory(UPLOAD_DIR);
			uploadService.cleanDirectory(RESULT_DIR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return Response.ok().build();
	}
	
	@RequestMapping(value = "input", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public InputSpreadsheet getSheetAtIndex(@QueryParam(value = "index") String index) {
		InputSpreadsheet insheet = null;
		try {
			List<Path> list = uploadService.getUploadedFilesPath(UPLOAD_DIR);
			Path filepath = list.get(Integer.valueOf(index));
			insheet = sjcService.loadInputSpreadsheet(filepath);			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
		return insheet;
	}
	
	@RequestMapping(value = "output", method = RequestMethod.GET)
	@Produces( {"application/vnd.ms-excel"} )
	public ResponseEntity<InputStreamResource> generateOutputSpreadsheet() {
		try {
			List<InputSpreadsheet> list = sjcService.loadInputSpreadsheetsFromDirectory(UPLOAD_DIR);
			OutputSpreadsheet outsheet = sjcService.generateOutputSpreadSheet(list);
			
			LocalDate now = LocalDate.now();
			Path resultFile = RESULT_DIR.resolve("Resultado_" + now.getDayOfMonth() + "_" + now.getMonthValue() + "_" + now.getYear() + ".xls");
			Path templateFile = RESOURCES_DIR.resolve("template_output.xlsx");
			sjcService.generateOuputSpreadsheetFile(resultFile, outsheet, templateFile);
			
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
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
	}
	
	@RequestMapping(value = "output/messages", method = RequestMethod.GET)
	@Produces( {"application/pdf"} )
	public ResponseEntity<InputStreamResource> getOutputMessagesFile() {
		OutputSpreadsheet outsheet = null;
		try {
			List<InputSpreadsheet> list = sjcService.loadInputSpreadsheetsFromDirectory(UPLOAD_DIR);
			outsheet = sjcService.generateOutputSpreadSheet(list);
			
			LocalDate now = LocalDate.now();
			Path resultFile = RESULT_DIR.resolve("Mensagens_" + now.getDayOfMonth() + "_" + now.getMonthValue() + "_" + now.getYear() + ".pdf");
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
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
	}
		
}
