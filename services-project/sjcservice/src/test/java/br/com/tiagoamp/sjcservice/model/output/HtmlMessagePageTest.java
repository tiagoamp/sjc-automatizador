package br.com.tiagoamp.sjcservice.model.output;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.tiagoamp.sjcservice.model.MessageType;
import br.com.tiagoamp.sjcservice.model.ProcessingMessage;

public class HtmlMessagePageTest {

	private HtmlMessagePage page;
	
	
	@Before
	public void setUp() throws Exception {
		page = new HtmlMessagePage();
	}

	@After
	public void tearDown() throws Exception {
		page = null;
	}
	
	
	private Map<String, List<ProcessingMessage>> getMapTestMessages() {
		Map<String, List<ProcessingMessage>> map = new HashMap<>();		
		ProcessingMessage msg01 = new ProcessingMessage(MessageType.ERROR, "msg-01");
		ProcessingMessage msg02 = new ProcessingMessage(MessageType.ERROR, "msg-02");
		ProcessingMessage msg03 = new ProcessingMessage(MessageType.ALERT, "msg-03");
		ProcessingMessage msg04 = new ProcessingMessage(MessageType.ALERT, "msg-04");		
		List<ProcessingMessage> msgs = new ArrayList<>();
		msgs.addAll(Arrays.asList(msg01, msg02, msg03, msg04));		
		map.put("filename1", msgs);				
		return map;
	}
	
	
	@Test
	public void testGenerate_bothMessageType_shouldGenerateBothLists() throws IOException {
		Map<String, List<ProcessingMessage>> map = getMapTestMessages();
		
		String result = page.generate(map);
		
		assertTrue("Must have all messages.", result.contains("msg-01"));
		assertTrue("Must have all messages.", result.contains("msg-02"));
		assertTrue("Must have all messages.", result.contains("msg-03"));
		assertTrue("Must have all messages.", result.contains("msg-04"));
	}
	
	@Test
	public void testGenerate_onlyAlertType_shouldGenerateNoErrorMessageText() throws IOException {
		Map<String, List<ProcessingMessage>> map = getMapTestMessages();
		List<ProcessingMessage> list = map.get("filename1");
		List<ProcessingMessage> errorList = new ArrayList<>();
		for (ProcessingMessage msg : list) {  // removing error msgs
			if (msg.getType() == MessageType.ERROR) errorList.add(msg);
		}
		list.removeAll(errorList);
		
		String result = page.generate(map);
		
		assertTrue("Must not have error message 01.", !result.contains("msg-01"));
		assertTrue("Must not have error message 02.", !result.contains("msg-01"));
		assertTrue("Must have alert message 03.", result.contains("msg-03"));
		assertTrue("Must have alert message 04.", result.contains("msg-04"));
		assertTrue("Must have 'no error message' text.", result.contains("Não foram geradas mensagens"));
		
	}

}
