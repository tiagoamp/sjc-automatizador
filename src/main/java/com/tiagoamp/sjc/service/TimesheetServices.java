package com.tiagoamp.sjc.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tiagoamp.sjc.dao.FeriadosReader;
import com.tiagoamp.sjc.dao.TimesheetWriter;
import com.tiagoamp.sjc.model.Timesheet;

@Service
public class TimesheetServices {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimesheetServices.class);
	
	private final Path HOLIDAYS_FILE = Paths.get("resources","feriados.txt");
	
	
	@Autowired
	private FeriadosReader feriadosReader;
	
	@Autowired
	private TimesheetWriter timesheetWriter;
	
	private List<LocalDate> holidays;
	
	
	public Path generate(YearMonth yearMonth, int extraHoursAmount) throws IOException {
		LOGGER.info("Generating timesheet for " + yearMonth + " with " + extraHoursAmount + " extra hours amount");
		if (holidays == null) loadHolidays();
		Timesheet timesheet = new Timesheet(yearMonth, extraHoursAmount, holidays);
		Map<LocalDate, String[]> dataPontos = timesheet.computeHoursEntriesPerDay();		
		Path spreadsheet = timesheetWriter.generateSpreadsheet(dataPontos, yearMonth);
		return spreadsheet;
	}
	
	
	private void loadHolidays() {
		try {
			feriadosReader.loadFromFile(HOLIDAYS_FILE);
			holidays = feriadosReader.getHolidays();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
}
