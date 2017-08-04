package br.com.tiagoamp.sjcservice.model.input;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.tiagoamp.sjcservice.model.ProcessingMessage;
import br.com.tiagoamp.sjcservice.model.fieldprocessor.NumericFieldProcessor;

public class NumericFieldProcessorTest {
	
	private NumericFieldProcessor inputFieldProcessor;

	
	@Before
	public void setUp() throws Exception {
		inputFieldProcessor = new NumericFieldProcessor("Test Field");
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
	public void testProcess_AlfanumericValue_shouldExcludeNonnumericsAndAddProcessMessage() {
		String result = inputFieldProcessor.process("123ABCD");
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("Result must be converted to only numeric characters", "123", result);
		assertEquals("Must contain one alert message.", 1, messages.size());
	}
	
	@Test
	public void testProcess_LiteralValue_shouldReturnZeroAndAddProcessMessage() {
		String result = inputFieldProcessor.process("ABCD");
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("Only literal iinput must result in zero value", "0", result);
		assertEquals("Must contain one alert message.", 1, messages.size());
	}
	
	@Test
	public void testProcess_EmptyValue_shouldReturnZero() {
		String result = inputFieldProcessor.process("");
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("Empty input must result in zero value", "0", result);
		assertEquals("Must not contain messages.", 0, messages.size());
	}
	
	@Test
	public void testProcess_NumericValue_shouldReturnSameNumericValue() {
		String numericInput = "123";
		String result = inputFieldProcessor.process(numericInput);
		List<ProcessingMessage> messages = inputFieldProcessor.getMessages();
		assertEquals("Numeric inout should return the same numeric value", numericInput, result);
		assertEquals("Must not contain messages.", 0, messages.size());
	}
	
}
