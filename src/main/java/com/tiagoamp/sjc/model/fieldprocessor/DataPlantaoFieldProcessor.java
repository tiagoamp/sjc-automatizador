package com.tiagoamp.sjc.model.fieldprocessor;

public class DataPlantaoFieldProcessor extends FieldProcessor {
	
	private String month;
	private String year;
	
	private final String REGEX_FULL_DATE = "\\d{1,2}[-|\\/]{1}[\\w]+[-|\\/]\\d+";
	private final String REGEX_DAY_MONTH = "\\d{1,2}[-|\\/]{1}[\\w]+";
	private final String REGEX_DAY_ONLY = "\\d{1,2}";
	
	
	public DataPlantaoFieldProcessor(String month, String year) {
		this.month = month;
		this.year = year;
	}
	
	
	public String process(String inputValue) {
		if (inputValue == null || inputValue.isEmpty() || !numericPattern.matcher(inputValue).find()) {
			return "";
		}
		
		if ( inputValue.matches(REGEX_FULL_DATE) ) {
			return inputValue;
		}
		
		if ( inputValue.matches(REGEX_DAY_MONTH) ) {
			return inputValue + "/" + year;
		}
		
		if ( inputValue.matches(REGEX_DAY_ONLY) ) {
			return inputValue + "/" + month + "/" + year;
		}
		
		return "";
	}
		
}
