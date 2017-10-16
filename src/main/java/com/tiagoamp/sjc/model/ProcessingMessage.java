package com.tiagoamp.sjc.model;

public class ProcessingMessage {
	
	public ProcessingMessage() {		
	}
	
	public ProcessingMessage(MessageType type, String text) {
		this.type = type;
		this.text = text;
	}
	
	
	private MessageType type;
	private String text;
	
	
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


}
