package com.tiagoamp.sjc.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

import org.junit.Test;

import com.tiagoamp.sjc.model.Timesheet;

public class TimesheetWriterTest {

	@Test
	public void testGenerateSpreadsheet() throws IOException {
		//given
		Path template = Paths.get("resources","ModeloPonto.xlsx");
		YearMonth yearMonth = YearMonth.of(2019, Month.NOVEMBER);
		Timesheet timesheet = new Timesheet(yearMonth, 35);
		Map<LocalDate, String[]> dataPontos = timesheet.computeHoursEntriesPerDay();
		TimesheetWriter timesheetWriter = new TimesheetWriter(dataPontos, yearMonth, template);
		// when
		timesheetWriter.generateSpreadsheet();
		// then
		Path result = Paths.get("folhaponto","ponto_" + yearMonth.getYear() + "_" + yearMonth.getMonthValue() + ".xlsx");
		assertTrue(Files.exists(result));
	}

}
