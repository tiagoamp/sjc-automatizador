package com.tiagoamp.sjc;

import java.io.File;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tiagoamp.sjc.model.input.InputSpreadsheet;
import com.tiagoamp.sjc.model.output.OutputSpreadsheet;
import com.tiagoamp.sjc.service.SjcServicesFacade;
import com.tiagoamp.sjc.service.UploadService;

@CrossOrigin
@RestController
@RequestMapping("/sjc")
public class SjcController {
	
	@Autowired
	private SjcServicesFacade sjcService;
	
	@Autowired
	private UploadService uploadService;
	
	private final Path UPLOAD_DIR = SjcAutoApplication.BASE_DIR.resolve("upload/");
	private final Path RESULT_DIR = SjcAutoApplication.BASE_DIR.resolve("resultado/");
	
		
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
			uploadService.cleanUploadDirectory(UPLOAD_DIR);
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
	public Response generateOutputSpreadsheet() {
		try {
			List<InputSpreadsheet> list = sjcService.loadInputSpreadsheetsFromDirectory(UPLOAD_DIR);
			OutputSpreadsheet outsheet = sjcService.generateOutputSpreadSheet(list);
			
			LocalDate now = LocalDate.now();
			Path resultFile = RESULT_DIR.resolve("Resultado_" + now.getDayOfMonth() + "_" + now.getMonthValue() + "_" + now.getYear() + ".xls");
			sjcService.generateOuputSpreadsheetFile(resultFile, outsheet);			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return Response.ok().build();
	}
	
	@RequestMapping(value = "output2", method = RequestMethod.GET, produces = "application/vnd.ms-excel")
	public @ResponseBody HttpEntity<byte[]> getOutputSpreadsheet2() {
		OutputSpreadsheet outsheet = null;
		try {
			List<InputSpreadsheet> list = sjcService.loadInputSpreadsheetsFromDirectory(UPLOAD_DIR);
			outsheet = sjcService.generateOutputSpreadSheet(list);
			
			LocalDate now = LocalDate.now();
			Path resultFile = RESULT_DIR.resolve("Resultado_" + now.getDayOfMonth() + "_" + now.getMonthValue() + "_" + now.getYear() + ".xls");
			sjcService.generateOuputSpreadsheetFile(resultFile, outsheet);
			
			byte[] document = FileCopyUtils.copyToByteArray(resultFile.toFile());
		    HttpHeaders header = new HttpHeaders();
		    header.setContentType(new org.springframework.http.MediaType("application", "vnd.ms-excel"));
		    header.set("Content-Disposition", "inline; filename=" + resultFile.getFileName());
		    header.setContentLength(document.length);
		    return new HttpEntity<byte[]>(document, header);
			
			//return new FileSystemResource(resultFile.toFile()); 
			
			// Generate the http headers with the file properties
	        /*HttpHeaders headers = new HttpHeaders();
	        headers.add("content-disposition", "attachment; filename=" + resultFile.toString());

	        // Split the mimeType into primary and sub types
	        String primaryType, subType;
	        try {
	            primaryType = "application";
	            subType = "vnd.ms-excel";
	        }
	            catch (IndexOutOfBoundsException | NullPointerException ex) {
	            return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
	        }

	        headers.setContentType( new org.springframework.http.MediaType(primaryType, subType) );

	        return new ResponseEntity<>(resultFile.toFile(), headers, HttpStatus.OK);*/
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
	}
	
	
}
