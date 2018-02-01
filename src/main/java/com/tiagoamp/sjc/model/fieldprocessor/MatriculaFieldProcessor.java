package com.tiagoamp.sjc.model.fieldprocessor;

import org.apache.commons.lang.StringUtils;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;

public class MatriculaFieldProcessor extends FieldProcessor {
	
	public MatriculaFieldProcessor() {
	}
	
	public String process(String inputValue) {
		if (inputValue == null) throw new IllegalArgumentException();
		String value = inputValue;
		if (nonNumericPattern.matcher(value).find()) { 
    		value = value.replaceAll(nonNumericPattern.pattern(), "");
    		messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém matrícula não numérica: '" + inputValue + "'. Caracteres não-numéricos retirados pelo sistema: " + value + "."));
    		inputValue = value;
    	}
		if (value.length() == 11 && value.startsWith("0")) {
			value = value.substring(1);
			messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém matrícula com 11 dígitos iniciada por zero: '" + inputValue + "'. Matrícula corrigida pelo sistema: " + value + "."));
			inputValue = value;
		}
		if (value.length() > 10) {
			messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém matrícula com mais de 10 dígitos: '" + value + "'. Matrícula NÃO corrigida pelo sistema!"));
		} else if (value.length() < 10) {
			value = StringUtils.leftPad(value, 10, "0");
			messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém matrícula < 10 dígitos: '" + inputValue + "'. Matrícula corrigida pelo sistema: completada com zeros à esquerda: " + value + "."));
			inputValue = value;
		}
		return value;
	}
		
}
