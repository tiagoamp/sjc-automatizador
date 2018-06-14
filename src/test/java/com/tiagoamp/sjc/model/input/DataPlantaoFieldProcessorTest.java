package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.assertEquals;

import java.time.Month;
import java.time.YearMonth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.fieldprocessor.DataPlantaoFieldProcessor;

public class DataPlantaoFieldProcessorTest {
	
	private DataPlantaoFieldProcessor inputFieldProcessor;

	
	@Before
	public void setUp() throws Exception {
		YearMonth yearMonth = YearMonth.of(2017, Month.OCTOBER);
		inputFieldProcessor = new DataPlantaoFieldProcessor(yearMonth);
	}

	@After
	public void tearDown() throws Exception {
		inputFieldProcessor = null;
	}

	
	@Test
	public void testProcess_invalidValue_shouldReturnEmptyString() {
		String result = inputFieldProcessor.process("1x");
		assertEquals("Result should be empty string for invalid input", "", result);		
	}
	
	@Test
	public void testProcess_emptyValue_shouldReturnEmptyString() {
		String result = inputFieldProcessor.process("");
		assertEquals("Result should be empty string for empty input", "", result);		
	}
	
	@Test
	public void testProcess_onlyDayInput_shouldReturnCompletedValue() {
		String result = inputFieldProcessor.process("12");
		assertEquals("Result should contains month and year concat.", "12/10/2017", result);		
	}
	
	@Test
	public void testProcess_dayMonthInput_shouldReturnCompletedValue() {
		String result = inputFieldProcessor.process("12/jun");
		assertEquals("Result should contains year concat.", "12/06/2017", result);		
	}
	
	@Test
	public void testProcess_dayMonthYearInput_shouldReturnSameValue() {
		String result = inputFieldProcessor.process("12/12/2012");
		assertEquals("Result should return same full input date.", "12/12/2012", result);		
	}
	
	@Test
	public void testProcess_dateSeparatedByDotInput_shouldReturnSameValue() {
		String result = inputFieldProcessor.process("12.12.2012");
		assertEquals("Result should return same full input date with slashs.", "12/12/2012", result);		
	}
	
}
