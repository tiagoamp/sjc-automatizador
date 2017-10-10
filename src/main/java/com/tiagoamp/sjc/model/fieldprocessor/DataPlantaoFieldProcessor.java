package com.tiagoamp.sjc.model.fieldprocessor;

import java.time.LocalDate;

public class DataPlantaoFieldProcessor extends FieldProcessor {
	
	private String month;
	private String year;
	
	private final String REGEX_FULL_DATE = "\\d{1,2}[-|\\/]{1}[\\w]+[-|\\/]\\d+";
	private final String REGEX_DAY_MONTH = "\\d{1,2}[-|\\/]{1}[\\w]+";
	private final String REGEX_DAY_ONLY = "\\d{1,2}";
	
	
	public DataPlantaoFieldProcessor(String month, String year) {
		this.month = month;
		this.year = year;
		
		if (this.month == null) this.month = String.valueOf(LocalDate.now().minusMonths(1).getMonthValue());
		if (this.year == null) this.year = String.valueOf(LocalDate.now().getYear());
		
		this.month = this.month.toLowerCase();
	}
	
	
	public String process(String inputValue) {
		if (inputValue == null || inputValue.isEmpty() || !numericPattern.matcher(inputValue).find()) {
			return "";
		}
		
		inputValue = inputValue.replace("-", "/").replace(".", "/").toLowerCase();
		
		if ( inputValue.matches(REGEX_FULL_DATE) ) {
			return inputValue.replace("[-]", "/").replace(".", "/");
		}
		
		if ( inputValue.matches(REGEX_DAY_MONTH) ) {
			String str[] = inputValue.split("/");
			return str[0] + "/" + MonthConverter.getConvertedMonthValue(str[1]) + "/" + year;
		}
		
		if ( inputValue.matches(REGEX_DAY_ONLY) ) {
			return inputValue + "/" + MonthConverter.getConvertedMonthValue(month) + "/" + year;
		}
		
		return "";
	}
		
}
