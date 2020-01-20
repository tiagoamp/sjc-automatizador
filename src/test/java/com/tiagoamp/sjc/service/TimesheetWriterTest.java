package com.tiagoamp.sjc.service;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

import org.junit.Test;

import com.tiagoamp.sjc.dao.TimesheetWriter;
import com.tiagoamp.sjc.model.Timesheet;

public class TimesheetWriterTest {

	@Test
	public void testGenerateSpreadsheet() throws IOException {
		//given
		YearMonth yearMonth = YearMonth.of(2019, Month.NOVEMBER);
		Timesheet timesheet = new Timesheet(yearMonth, 35);
		Map<LocalDate, String[]> dataPontos = timesheet.computeHoursEntriesPerDay();
		TimesheetWriter timesheetWriter = new TimesheetWriter();
		// when
		Path result = timesheetWriter.generateSpreadsheet(dataPontos, yearMonth);
		// then
		assertTrue(Files.exists(result));
	}

}
