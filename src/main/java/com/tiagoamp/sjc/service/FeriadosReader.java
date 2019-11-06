package com.tiagoamp.sjc.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FeriadosReader {
	
	private List<String> lines;
	private DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
	
	public List<String> loadFromFile(Path filePath) throws IOException {
		List<String> lines = Files.lines(filePath).collect(Collectors.toList());
		return lines;
	}
	
	public List<LocalDate> getHolidays() {
		final int startYear = 2010, endYear = 2030;
		
		List<LocalDate> feriadosMoveis = lines.stream()
				.map(text -> {
					if (text.matches("\\d{1,2}\\/\\d{1,2}\\/\\d{2,4}\\s*")) {
						LocalDate date = LocalDate.parse(text.trim(), fullDateFormatter);
						return date;
					} 
					return null;
				})
				.filter(d -> d != null).collect(Collectors.toList());
		
		 List<LocalDate> feriadosFixos = lines.stream()
				.map(text -> {
					if (text.matches("\\d{1,2}\\/\\d{1,2}\\s*")) {
						List<LocalDate> dates = IntStream.rangeClosed(startYear, endYear)
							.mapToObj(y -> LocalDate.parse(text.trim() + "/" + y, fullDateFormatter))
							.collect(Collectors.toList());
						return dates;
					}
					return null;
				})
				.filter(d -> d != null)
				.flatMap(Collection::stream).collect(Collectors.toList());
		
		 List<LocalDate> feriados = new ArrayList<>();
		 feriados.addAll(feriadosMoveis);
		 feriados.addAll(feriadosFixos);
		 return feriados;
	}
	
	
	public void setLines(List<String> lines) {
		this.lines = lines;
	}

}
