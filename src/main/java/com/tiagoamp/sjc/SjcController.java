package com.tiagoamp.sjc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tiagoamp.sjc.model.input.InputSpreadsheet;
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
	
		
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public Response uploadFile(MultipartHttpServletRequest request) {
		try {
			Iterator<String> itr = request.getFileNames();
			while (itr.hasNext()) {
				String uploadedFile = itr.next();
				MultipartFile mfile = request.getFile(uploadedFile);
				String filepathStr = SjcAutoApplication.UPLOAD_DIR.toString() + File.separator +  mfile.getOriginalFilename();
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
			total = uploadService.getNumberOfFilesInUploadDirectory(SjcAutoApplication.UPLOAD_DIR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}
		return String.valueOf(total);
	}
	
	@RequestMapping(value = "input", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public InputSpreadsheet getAt(@QueryParam(value = "index") String index) {
		InputSpreadsheet insheet = null;
		try {
			List<Path> list = uploadService.getUploadedFilesPath(SjcAutoApplication.UPLOAD_DIR);
			Path filepath = list.get(Integer.valueOf(index));
			insheet = sjcService.loadInputSpreadsheet(filepath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
		return insheet;
	}	
	
}
