package com.tiagoamp.sjc.model.input;

import static org.junit.Assert.assertEquals;

import java.time.YearMonth;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;

public class InputSpreadsheetTest {
	
	private InputSpreadsheet spreadsheet;

	@Before
	public void setUp() throws Exception {
		spreadsheet = new InputSpreadsheet();
	}

	@After
	public void tearDown() throws Exception {
		spreadsheet = null;
	}

	
	@Test
	public void getMessages_hasOnlySpreadsheetMessages_shouldReturnOuterMessages() {
		// given
		ProcessingMessage spreadsheetMessage = new ProcessingMessage(MessageType.ERROR,"MSG00");
		spreadsheet.getMessages().add(spreadsheetMessage);
		InSheet sheet =  new InSheet(SjcGeneralCode.ADMINISTRATIVO, YearMonth.now());
		spreadsheet.getSheets().put(sheet.getCode(), sheet);		
		// when
		List<ProcessingMessage> allMessages = spreadsheet.getMessages();		
		// then
		assertEquals(1, allMessages.size());
	}
	
}
