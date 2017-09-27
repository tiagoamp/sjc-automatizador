package com.tiagoamp.sjc;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tiagoamp.sjc.service.SjcServicesFacade;

@CrossOrigin
@RestController
@RequestMapping("/sjc")
public class SjcController {
	
	@Autowired
	private SjcServicesFacade service;
	
	
	@RequestMapping(method=RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	public String home() {
		return "tiagoamp";		
	}
	
	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public Response uploadFile(MultipartHttpServletRequest request) {
		try {
			Iterator<String> itr = request.getFileNames();
			while (itr.hasNext()) {
				String uploadedFile = itr.next();
				MultipartFile mfile = request.getFile(uploadedFile);				
				File dest = new File(SjcAutoApplication.UPLOAD_DIR.toString() + File.separator +  mfile.getOriginalFilename());
				mfile.transferTo(dest);				
			}
		} catch (IOException e) {
			e.printStackTrace();			
			return Response.serverError().build();
		}
		return Response.ok().build();
	}
	
	
}
