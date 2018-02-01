package com.tiagoamp.sjc.model.fieldprocessor;

public class MonthConverter {
	
	public static String getConvertedMonthValue(String input) {
		if (input.equals("1") || input.equals("janeiro") || input.equals("jan") || input.equals("january") ) return "01";
		if (input.equals("2") || input.equals("fevereiro") || input.equals("fev") || input.equals("feb") || input.equals("february") ) return "02";
		if (input.equals("3") || input.equals("março") || input.equals("mar") || input.equals("march") ) return "03";
		if (input.equals("4") || input.equals("abril") || input.equals("abr") || input.equals("apr") || input.equals("april") ) return "04";
		if (input.equals("5") || input.equals("maio") || input.equals("mai") || input.equals("may") ) return "05";
		if (input.equals("6") || input.equals("junho") || input.equals("jun") || input.equals("june") ) return "06";
		if (input.equals("7") || input.equals("julho") || input.equals("jul") || input.equals("july") ) return "07";
		if (input.equals("8") || input.equals("agosto") || input.equals("ago") || input.equals("aug") || input.equals("august") ) return "08";
		if (input.equals("9") || input.equals("setembro") || input.equals("set") || input.equals("sep") || input.equals("september") ) return "09";
		if (input.equals("10") || input.equals("outubro") || input.equals("out") || input.equals("oct") || input.equals("october") ) return "10";
		if (input.equals("11") || input.equals("novembro") || input.equals("nov") || input.equals("november") ) return "11";
		if (input.equals("12") || input.equals("dezembro") || input.equals("dez") || input.equals("dec") || input.equals("december") ) return "12";
		
		return input;
		
	}

}
