package br.com.tiagoamp.sjcservice.model.fieldprocessor;

import br.com.tiagoamp.sjcservice.model.MessageType;
import br.com.tiagoamp.sjcservice.model.ProcessingMessage;

public class NumericFieldProcessor extends FieldProcessor {
	
	private String fieldName;
	
	public NumericFieldProcessor(String fieldName) {
		this.fieldName = fieldName;
	}
	    
    public String process(String inputValue) {
		if (inputValue == null) throw new IllegalArgumentException();
		String value = inputValue;
		if (nonNumericPattern.matcher(value).find()) {
    		value = value.replaceAll(nonNumericPattern.pattern(), "");
    		messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém '" + fieldName + "' não numérico: '" + inputValue + "'. Valor corrigido pelo sistema: " + value + "."));
    	}
		if (value.isEmpty()) value = "0";
    	return value;
	}
    
}
