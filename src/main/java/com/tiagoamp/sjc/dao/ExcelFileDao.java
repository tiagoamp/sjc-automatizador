package com.tiagoamp.sjc.dao;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.input.v3.ConvHeader;
import com.tiagoamp.sjc.model.input.v3.ConvRow;
import com.tiagoamp.sjc.model.input.v3.ConvertedSheet;
import com.tiagoamp.sjc.model.input.v3.ConvertedSpreadsheet;

@Repository
public class ExcelFileDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileDao.class);
	
	public Path createConvertedSpreadsheet(ConvertedSpreadsheet convSpreadsheet, String convertedFileName) throws FileNotFoundException, IOException {
		LOGGER.info("Convertendo arquivo a partir de " + convSpreadsheet.getOriginalFile().getFileName());
		Path convFilePath = convSpreadsheet.getOriginalFile().getParent().resolve(convertedFileName);		
		try ( XSSFWorkbook workbook = new XSSFWorkbook();
			  FileOutputStream fos = new FileOutputStream(convFilePath.toFile()); ) 
			{
				Map<Integer, String[]> headerMap = createHeaderMap(convSpreadsheet.getHeader());
				
				for (SjcGeneralCode code : SjcGeneralCode.values()) {
					ConvertedSheet convSheet = convSpreadsheet.getConvertedSheets().get(code);
					if (convSheet == null || convSheet.getRows() == null || convSheet.getRows().size() == 0) continue;									
					Map<Integer, String[]> dataMap = this.createDataMap(convSheet, code);					
					XSSFSheet xsheet = workbook.createSheet(code.toString());					
					fillRowsInExcelSheet(xsheet, headerMap, dataMap);						
					adjustColumnsSize(xsheet);
				}
				
				workbook.write(fos);
			}
		return convFilePath;
	}
	
	
	private Map<Integer, String[]> createHeaderMap(ConvHeader header) {
		Map<Integer, String[]> data = new HashMap<>();		
		data.put(0, new String[] {"Título:", header.getFixedTitle()});
		data.put(1, new String[] {"Nome da Unidade:", header.getNomeUnidadePrisional()});
		data.put(2, new String[] {"Mês:", header.getYearMonthRef() != null ? header.getMonthRefAsStr() : "NÃO IDENTIFICADO"});
		data.put(3, new String[] {"Ano:", header.getYearMonthRef() != null ? header.getYearRefAsStr() : "NÃO IDENTIFICADO"});		
		return data;		
	}
	
	private Map<Integer, String[]> createDataMap(ConvertedSheet cnvSheet, SjcGeneralCode code) {
		Map<Integer, String[]> data = new HashMap<>();
		Integer counter = 0;
		if (code == SjcGeneralCode.ADMINISTRATIVO) {
			data.put(counter, new String[] {"MATRÍCULA", "NOME DO SERVIDOR", "HORA EXTRA", "ADICIONAL NOTURNO"});
		} else {
			data.put(counter, new String[] {"MATRÍCULA", "NOME DO SERVIDOR", "HORA EXTRA", "ADICIONAL NOTURNO", "PLANTÃO 1", "PLANTÃO 2", "PLANTÃO 3", "PLANTÃO 4", "PLANTÃO 5", "TOTAL DE PLANTÕES"});
		}		
		for (ConvRow row : cnvSheet.getRows()) {
			if (code == SjcGeneralCode.ADMINISTRATIVO) {
				data.put(++counter, new String[] {row.getMatricula(), row.getNome().toUpperCase(), row.getQtdHoraExtra(), row.getQtdAdicionalNoturno()});								
			} else {
				data.put(++counter, new String[] {row.getMatricula(), row.getNome().toUpperCase(), row.getQtdHoraExtra(), row.getQtdAdicionalNoturno(), 
					    row.getDtPlantoesExtras()[0], row.getDtPlantoesExtras()[1], row.getDtPlantoesExtras()[2], row.getDtPlantoesExtras()[3], row.getDtPlantoesExtras()[4], 
					    row.getQtdPlantoesExtra()});	
			}			
    	}
		return data;
	}
	
	private void fillRowsInExcelSheet(XSSFSheet xssfsheet, Map<Integer, String[]> headerMap, Map<Integer, String[]> dataMap) {
		Set<Integer> headerRows = headerMap.keySet(); // Sets to Iterate and add rows into XLS file
		Set<Integer> dataRows = dataMap.keySet(); 
		int rownum = xssfsheet.getLastRowNum(); // get the last row number to append new data   
		
		for (Integer key : headerRows) {
			Row row = xssfsheet.createRow(rownum++); // Creating a new Row in existing XLSX sheet
			String[] headersArr = headerMap.get(key);
			int cellnum = 0;			
			Cell cellKey = row.createCell(cellnum++);			
			cellKey.setCellStyle(getBoldCellStyle(xssfsheet));
			cellKey.setCellValue(headersArr[0]);
			Cell cellValue = row.createCell(cellnum++);
			cellValue.setCellValue(headersArr[1]);
		}
		
		Row emptyRow = xssfsheet.createRow(rownum++); 
		emptyRow.createCell(0);
		
		Row row = xssfsheet.createRow(rownum++); 
		String[] colsNamesArr = dataMap.get(0);
		int cellnum = 0;
		for (int i=0; i < colsNamesArr.length; i++ ) {
			String colName = colsNamesArr[i];
			Cell cellForColName = row.createCell(cellnum++);			
			cellForColName.setCellStyle(getBoldCellStyle(xssfsheet));
			cellForColName.setCellValue(colName);
		}
		dataMap.remove(0); // removes cols names already put in the file
		
		for (Integer key : dataRows) {			
			row = xssfsheet.createRow(rownum++); 
			cellnum = 0;
			String[] colsValsArr = dataMap.get(key);
			for (int i=0; i < colsValsArr.length; i++ ) { 
				String value = colsValsArr[i];			
				Cell cellForColValue = row.createCell(cellnum++);
				cellForColValue.setCellValue(value);
			}			
		}		
	}
	
	private XSSFCellStyle getBoldCellStyle(XSSFSheet xssfsheet) {
		XSSFFont font = xssfsheet.getWorkbook().createFont();
		font.setBold(true);
		XSSFCellStyle style = xssfsheet.getWorkbook().createCellStyle();
		style.setFont(font);		
		return style;
	}
	
	private void adjustColumnsSize(XSSFSheet xsheet) {
		int numberOfColumns = 11;
		for (int i = 0; i < numberOfColumns; i++) {
			xsheet.autoSizeColumn(i); // column adjusting
		}
	}
	
}
