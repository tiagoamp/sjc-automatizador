package com.tiagoamp.sjc.model.input.v3.to;

import java.util.List;

import com.tiagoamp.sjc.model.ProcessingMessage;

public class ProcessedFileTO {

	private String fileName;
	private List<ProcessingMessage> messages;
	
	
	public ProcessedFileTO() { }
			
	public ProcessedFileTO(String fileName, List<ProcessingMessage> messages) {
		this.fileName = fileName;
		this.messages = messages;
	}


	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<ProcessingMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ProcessingMessage> messages) {
		this.messages = messages;
	}
		
}
