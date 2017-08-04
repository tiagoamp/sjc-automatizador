package br.com.tiagoamp.sjcservice.model.fieldprocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import br.com.tiagoamp.sjcservice.model.ProcessingMessage;

public abstract class FieldProcessor {
	
	protected List<ProcessingMessage> messages = new ArrayList<>();

	public static final Pattern nonNumericPattern = Pattern.compile("[^0-9]");
	public static final Pattern NumericPattern = Pattern.compile("[0-9]");
    
    public abstract String process(String inputValue);
	
    public List<ProcessingMessage> getMessages() {
		return messages;
	}
		
}
