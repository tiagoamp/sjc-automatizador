package com.tiagoamp.sjc.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TimesheetWriter {
	
	private Map<LocalDate, String[]> dataPontos;
	private YearMonth yearMonth;
	private Path template;
	private Path filePath;
	
	
	public TimesheetWriter(Map<LocalDate, String[]> dataPontos, YearMonth yearMonth, Path template) {
		this.dataPontos = dataPontos;
		this.yearMonth = yearMonth;
		this.template = template;
	}
	
	
	public void generateSpreadsheet() throws IOException {
		copyFileFromTemplate();
		
		FileInputStream fileIS = new FileInputStream(filePath.toFile());
		XSSFWorkbook workbook = new XSSFWorkbook(fileIS);
        XSSFSheet sheet = workbook.getSheetAt(3);
        
        XSSFRow rowYearMonth = sheet.getRow(2);
        XSSFCell cellYearMonth = rowYearMonth.getCell(7);
        cellYearMonth.setCellValue(DateTimeFormatter.ofPattern("MM/yyyy").format(yearMonth));
        
        TreeSet<LocalDate> sortedDays = getSortedKeyFromHours();
        
        int beforeInitRow = 11;
        Iterator<LocalDate> iterator = sortedDays.iterator();
        LocalDate date = iterator.next();
        
        for (int i=1; i<=31; i++) {
        	if (i == date.getDayOfMonth()) {
        		XSSFRow row = sheet.getRow(beforeInitRow + i);
        		if (row == null) sheet.createRow(beforeInitRow + i);
        		String[] pontos = dataPontos.get(date);
        		
        		XSSFCell ptoEntrada = row.getCell(1) == null ? row.createCell(1) : row.getCell(1);
        		XSSFCell ptoRefeicao = row.getCell(3) == null ? row.createCell(3) : row.getCell(3);
        		XSSFCell ptoRetorno = row.getCell(5) == null ? row.createCell(5) : row.getCell(5);
        		XSSFCell ptoSaida = row.getCell(7) == null ? row.createCell(7) : row.getCell(7);
        		
        		ptoEntrada.setCellValue(pontos[0]);
        		ptoSaida.setCellValue(pontos[pontos.length-1]);
        		
        		if (pontos.length == 4) {
        			ptoRefeicao.setCellValue(pontos[1]);
        			ptoRetorno.setCellValue(pontos[2]);
        		}
        		
        		if (iterator.hasNext()) date = iterator.next();
        		else break;        		
        	}
        }
        fileIS.close();
        
        FileOutputStream fileOS = new FileOutputStream(filePath.toFile());
        workbook.write(fileOS);
        
        fileOS.close();
        workbook.close();
	}
	
	private void copyFileFromTemplate() throws IOException {
		filePath = Paths.get("folhaponto","ponto_" + DateTimeFormatter.ofPattern("yyyy_MM").format(yearMonth) + ".xlsx");
		Files.deleteIfExists(filePath);
		Files.createDirectories(filePath);
		Files.copy(template, filePath, StandardCopyOption.REPLACE_EXISTING);
	}
	
	private TreeSet<LocalDate> getSortedKeyFromHours() {
		Set<LocalDate> keys = dataPontos.keySet();
		TreeSet<LocalDate> sorted = new TreeSet<>(keys);
		return sorted;
	}

}
