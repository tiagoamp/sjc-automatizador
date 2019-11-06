package com.tiagoamp.sjc;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.YearMonth;

import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tiagoamp.sjc.service.TimesheetServices;

@CrossOrigin
@RestController
@RequestMapping("/sjc/ponto")
public class SjcTimesheetController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SjcTimesheetController.class);
	
	@Autowired
	private TimesheetServices timesheetService;
	
	
	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public Response generateTimesheetSpreadsheet(@RequestParam(value="monthVal", required=true) String monthVal, 
			@RequestParam(value="yearVal", required=true) String yearVal, @RequestParam(value="extraHoursVal", required=true) String extraHoursVal) {
		try {
			int extraHoursAmount = Integer.valueOf(extraHoursVal);
			YearMonth yearMonth = YearMonth.of(Integer.valueOf(yearVal), Integer.valueOf(monthVal));
			Path file = timesheetService.generate(yearMonth, extraHoursAmount);
			return Response.created(URI.create(file.getFileName().toString())).build();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResponseProcessingException(Response.serverError().build(),e);
		}		
	}
			
}
