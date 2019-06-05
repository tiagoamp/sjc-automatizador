package com.tiagoamp.sjc.model.fieldprocessor;

import java.time.Month;
import java.time.YearMonth;
import java.util.Optional;

public class MonthConverter {
	
	public static Optional<Month> getConvertedMonth(String input) {
		Optional<Month> month = Optional.empty();
		if (input == null) return month;
		input = input.toLowerCase();
		if (input.equals("1") || input.equals("janeiro") || input.equals("jan") || input.equals("january") ) month = Optional.of(Month.JANUARY);
		if (input.equals("2") || input.equals("fevereiro") || input.equals("fev") || input.equals("feb") || input.equals("february") ) month = Optional.of(Month.FEBRUARY);
		if (input.equals("3") || input.equals("março") || input.equals("mar") || input.equals("march") ) month = Optional.of(Month.MARCH);
		if (input.equals("4") || input.equals("abril") || input.equals("abr") || input.equals("apr") || input.equals("april") ) month = Optional.of(Month.APRIL);
		if (input.equals("5") || input.equals("maio") || input.equals("mai") || input.equals("may") ) month = Optional.of(Month.MAY);
		if (input.equals("6") || input.equals("junho") || input.equals("jun") || input.equals("june") ) month = Optional.of(Month.JUNE);
		if (input.equals("7") || input.equals("julho") || input.equals("jul") || input.equals("july") ) month = Optional.of(Month.JULY);
		if (input.equals("8") || input.equals("agosto") || input.equals("ago") || input.equals("aug") || input.equals("august") ) month = Optional.of(Month.AUGUST);
		if (input.equals("9") || input.equals("setembro") || input.equals("set") || input.equals("sep") || input.equals("september") ) month = Optional.of(Month.SEPTEMBER);
		if (input.equals("10") || input.equals("outubro") || input.equals("out") || input.equals("oct") || input.equals("october") ) month = Optional.of(Month.OCTOBER);
		if (input.equals("11") || input.equals("novembro") || input.equals("nov") || input.equals("november") ) month = Optional.of(Month.NOVEMBER);
		if (input.equals("12") || input.equals("dezembro") || input.equals("dez") || input.equals("dec") || input.equals("december") ) month = Optional.of(Month.DECEMBER);		
		return month;
	}
	
	public static Optional<YearMonth> getYearMonthFrom(String month, String year) {
		Optional<YearMonth> result = Optional.empty();
		Optional<Month> convertedMonth = getConvertedMonth(month);
		if ( convertedMonth.isPresent() && year.matches("^\\d+$")) {
			YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), convertedMonth.get());
			result = Optional.of(yearMonth);
		}		
		return result;
	}

}
