package com.tiagoamp.sjc.dao;

import static com.tiagoamp.sjc.model.input.v3.InputLayoutConstants.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
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
	
	private DataFormatter df = new DataFormatter();
	
	
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
	
	public List<ConvertedSpreadsheet> loadFromDirectory(Path dir) throws FileNotFoundException, IOException {
		
	}
	
	public ConvertedSpreadsheet loadFrom(Path file) throws FileNotFoundException, IOException {
		LOGGER.info("Carregando arquivo: " + file.getFileName());
		
		ConvertedSpreadsheet spreadsheet = new ConvertedSpreadsheet();
		spreadsheet.setConvertedFile(file);
		
		try ( FileInputStream fis = new FileInputStream(file.toFile());
			  XSSFWorkbook xssworkbook = new XSSFWorkbook(fis); ) 
			{
				for (SjcGeneralCode code : SjcGeneralCode.values()) {
					XSSFSheet xsheet = xssworkbook.getSheet(code.toString());
					if (xsheet == null) continue;
					
					if (!verifySpreadsheetLayout(xsheet)) {
						LOGGER.error("Planilha " + file.getFileName().toString() + " tem problemas de layout e será desconsiderada!");
						continue;
					}
					
					ConvHeader header = loadHeader(xsheet);
					List<ConvRow> rows = loadRows(xsheet, code);
					ConvertedSheet sheet = new ConvertedSheet(code, header, rows);					
					
					if (spreadsheet.getHeader() == null) spreadsheet.setHeader(header);
					spreadsheet.getConvertedSheets().put(code, sheet);						            	            	
				}				
			}
		return spreadsheet;
	}
	
	public boolean verifySpreadSheetLayout(Path file) throws FileNotFoundException, IOException {
		try ( FileInputStream fis = new FileInputStream(file.toFile());
			  XSSFWorkbook xssworkbook = new XSSFWorkbook(fis); ) 
			{
				for (SjcGeneralCode code : SjcGeneralCode.values()) {
					XSSFSheet xsheet = xssworkbook.getSheet(code.toString());
					if (xsheet == null) continue;					
					if (!verifySpreadsheetLayout(xsheet)) return false;											            	            	
				}				
			} catch (OLE2NotOfficeXmlFileException e) {
				LOGGER.error("Arquivo " + file.getFileName() + " não é um formato válido do MS-Office.");
				return false;
			}
		return true;
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
	
	private boolean verifySpreadsheetLayout(XSSFSheet xsheet) {
		CellAddress unidTitleCellAddr = new CellAddress(CELL_ADDRESS_TITLE_LOTACAO);
		CellAddress yearTitleCellAddr = new CellAddress(CELL_ADDRESS_TITLE_ANO);
		CellAddress monthTitleCellAddr = new CellAddress(CELL_ADDRESS_TITLE_MES);
		String unidade = df.formatCellValue(xsheet.getRow(unidTitleCellAddr.getRow()).getCell(unidTitleCellAddr.getColumn()));
		String yearStr = df.formatCellValue(xsheet.getRow(yearTitleCellAddr.getRow()).getCell(yearTitleCellAddr.getColumn()));
		String monthStr = df.formatCellValue(xsheet.getRow(monthTitleCellAddr.getRow()).getCell(monthTitleCellAddr.getColumn()));
		boolean isValidHeader = unidade.equalsIgnoreCase("Nome da Unidade:") && yearStr.equalsIgnoreCase("Ano:") && monthStr.equalsIgnoreCase("Mês:");
		if (!isValidHeader) return false;
				
		for (int col = 0; col <= INDEX_COLUMN_ADICIONAL_NOTURNO; col++) {
			CellAddress colCellAddr = new CellAddress((INDEX_DATA_INIT_ROW-1), col);
			String value = df.formatCellValue(xsheet.getRow(colCellAddr.getRow()).getCell(colCellAddr.getColumn()));
			if (value == null) return false;
			value = value.trim();
			if (col == INDEX_COLUMN_MATRICULA && !value.equalsIgnoreCase("MATRÍCULA")) return false;            	
            else if (col == INDEX_COLUMN_NOME && !value.equalsIgnoreCase("NOME DO SERVIDOR")) return false;
            else if (col == INDEX_COLUMN_HORA_EXTRA && !value.equalsIgnoreCase("HORA EXTRA")) return false;
            else if (col == INDEX_COLUMN_ADICIONAL_NOTURNO && !value.equalsIgnoreCase("ADICIONAL NOTURNO")) return false;
		}
		
		return true;
	}
	
	private ConvHeader loadHeader(XSSFSheet xsheet) {
		CellAddress unidCellAddr = new CellAddress(CELL_ADDRESS_LOTACAO);
		CellAddress yearCellAddr = new CellAddress(CELL_ADDRESS_ANO);
		CellAddress monthCellAddr = new CellAddress(CELL_ADDRESS_MES);		
		String unidade = df.formatCellValue(xsheet.getRow(unidCellAddr.getRow()).getCell(unidCellAddr.getColumn()));
		String yearStr = df.formatCellValue(xsheet.getRow(yearCellAddr.getRow()).getCell(yearCellAddr.getColumn()));
		String monthStr = df.formatCellValue(xsheet.getRow(monthCellAddr.getRow()).getCell(monthCellAddr.getColumn()));				
		return new ConvHeader(unidade, monthStr, yearStr);		
	}
	
	private List<ConvRow> loadRows(XSSFSheet xsheet, SjcGeneralCode code) {
		List<ConvRow> rows = new ArrayList<>();
        Iterator<Row> rowItr = xsheet.iterator();
        if (!rowItr.hasNext()) return rows;
        Row row = goToInitialDataRow(rowItr);
		boolean endOfData = false;

        while (rowItr.hasNext() && !endOfData) {  // for each row
        	row = rowItr.next();
        	ConvRow cnvRow = new ConvRow();
                             
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {  // for each cell/column
                Cell cell = cellIterator.next();
                String value = df.formatCellValue(cell);
                
                if (cell.getColumnIndex() == INDEX_COLUMN_MATRICULA) {
                	if (value == null || value.isEmpty() || isEndOfData(value)) {
                		endOfData = true;
                		break;
                	}
                	cnvRow.setMatricula(value);
                } else if (cell.getColumnIndex() == INDEX_COLUMN_NOME) {
                	cnvRow.setNome(value);
                } else if (cell.getColumnIndex() == INDEX_COLUMN_HORA_EXTRA) {
                	cnvRow.setQtdHoraExtra(value);
                } else if (cell.getColumnIndex() == INDEX_COLUMN_ADICIONAL_NOTURNO) {
                	cnvRow.setQtdAdicionalNoturno(value);
                } else if (code == SjcGeneralCode.OPERACIONAL && cell.getColumnIndex() == INDEX_COLUMN_PLANTOES_EXTRAS) {
                	cnvRow.setQtdPlantoesExtra(value);
                } else if ( (code == SjcGeneralCode.OPERACIONAL) && 
                	   (cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_01 || cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_02 || 
                	    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_03 || cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_04 || 
                	    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_05) 
                	   ) {                	
                	int indexPlantao = cell.getColumnIndex() - INDEX_COLUMN_PLANTOESEXTRAS_01; // getting index of 'plantao' from 0 to 4 (there may be 5 plantoes)
                	cnvRow.getDtPlantoesExtras()[indexPlantao] = value;
                } //end columns
             }
            
            if (!endOfData && cnvRow.getMatricula() != null) rows.add(cnvRow);            
        }  // end rows
        return rows;
	}
	
	private Row goToInitialDataRow(Iterator<Row> rowItr) {
		Row row = rowItr.next();
        while ( rowItr.hasNext() && row.getRowNum() < INDEX_DATA_INIT_ROW - 1 ) row = rowItr.next();
        return row;
	}
	
	private boolean isEndOfData(String value) {
		return value.startsWith("Tipos de afastamentos") || value.startsWith("Este relatório deverá ser encaminhado");
	}
	
}
