package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.fieldprocessor.MatriculaFieldProcessor;

public class MatriculaFieldProcessorTest {
	
	private MatriculaFieldProcessor inputFieldProcessor;

	
	@Before
	public void setUp() throws Exception {
		inputFieldProcessor = new MatriculaFieldProcessor();
	}

	@After
	public void tearDown() throws Exception {
		inputFieldProcessor = null;
	}

	
	@Test(expected=IllegalArgumentException.class)
	public void testProcess_nullValueArgument_shouldThrowException() {
		inputFieldProcessor.process(null);
	}
	
	@Test
	public void testProcess_nonNumericValue_shouldExcludeNonnumericAndAddProcessMessage() {
		String result = inputFieldProcessor.process("1A2B3C4567890-X!@#$%&*()");
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("'Matrícula' should be converted to only numeric characters.", "1234567890", result);
		assertEquals("Must contain one alert message.", 1, messages.size());
	}
	
	@Test
	public void testProcess_ElevenDigitsAndStartedByZero_shouldExcludeFirstZeroValueAndAddProcessMessage() {
		String result = inputFieldProcessor.process("01234567890");
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("'Matrícula' should skip first zero character.", "1234567890", result);
		assertEquals("Must contain one alert message.", 1, messages.size());
	}
	
	@Test
	public void testProcess_ValueGreaterThan10Digits_shouldReturnSameValueAndAddProcessMessage() {
		String bigMatricula = "12345678901";
		String result = inputFieldProcessor.process(bigMatricula);
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("'Matrícula' greater than 10 digits must return the same.", bigMatricula, result);
		assertEquals("Must contain one alert message.", 1, messages.size());
	}
	
	@Test
	public void testProcess_ValueLessThan10Digits_shouldZeroLeftPadValueAndAddProcessMessage() {
		String result = inputFieldProcessor.process("12345");
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("'Matrícula' less than 10 digits must return zero left padded value.", "00000" + "12345", result);
		assertEquals("Must contain one alert message.", 1, messages.size());
	}
	
}
