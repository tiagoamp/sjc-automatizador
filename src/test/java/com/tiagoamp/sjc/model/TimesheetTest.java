package com.tiagoamp.sjc.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

import org.junit.Test;

public class TimesheetTest {

	private Timesheet timesheet;
	
	
	@Test
	public void testComputeHoursEntriesPerDay() {
		timesheet = new Timesheet(YearMonth.of(2019, Month.OCTOBER), 37);
		Map<LocalDate, String[]> result = timesheet.computeHoursEntriesPerDay();
		timesheet.printValues();
		assertNotNull(result);
	}

}
