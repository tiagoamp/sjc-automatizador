package com.tiagoamp.sjc.model.fieldprocessor;

import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

public class DataPlantaoFieldProcessor extends FieldProcessor {
	
	private YearMonth yearMonth;
	
	private final String REGEX_FULL_DATE = "\\d{1,2}[-|\\/]{1}[\\w]+[-|\\/]\\d+";
	private final String REGEX_DAY_MONTH = "\\d{1,2}[-|\\/]{1}[\\w]+";
	private final String REGEX_DAY_ONLY = "\\d{1,2}";
	
	
	public DataPlantaoFieldProcessor(YearMonth yearMonthParam) {
		this.yearMonth = yearMonthParam;
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
			Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(str[1].toLowerCase());
			Month month = convertedMonth.orElse(yearMonth.getMonth());
			return str[0] + "/" + String.format("%02d", month.getValue()) + "/" + yearMonth.getYear();
		}
		
		if ( inputValue.matches(REGEX_DAY_ONLY) ) {
			return inputValue + "/" + yearMonth.getMonthValue() + "/" + yearMonth.getYear();
		}
		
		return "";
	}
		
}
