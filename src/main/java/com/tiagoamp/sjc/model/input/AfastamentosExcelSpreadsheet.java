package com.tiagoamp.sjc.model.input;

import static com.tiagoamp.sjc.model.input.InputLayoutConstants.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tiagoamp.sjc.model.fieldprocessor.FieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.MatriculaFieldProcessor;

public class AfastamentosExcelSpreadsheet {
	
	private HistoricoAfastamentos afastamentos;
	private Path filePath; 
	
	
	public AfastamentosExcelSpreadsheet(Path inputFile) {
		this.filePath = inputFile;
		afastamentos = new HistoricoAfastamentos();				
	}
		
	public HistoricoAfastamentos loadFromFile() throws IOException {
		afastamentos.setFileName(filePath.getFileName().toString());
		
		if (!filePath.getFileName().toString().toLowerCase().endsWith(".xlsx")) {
			throw new IllegalArgumentException("Planilha de Afastamentos sem extensão '.xlsx'");
		}
		
		try ( FileInputStream fis = new FileInputStream(filePath.toFile());
			  XSSFWorkbook xssworkbook = new XSSFWorkbook(fis); ) 
			{
			XSSFSheet xssfsheet = xssworkbook.getSheetAt(0);
			
			if (xssfsheet == null) {
				throw new IllegalArgumentException("Não foi possível ler planilha de Histórico de Afastamentos.");
            } 
			
			AfastamentoSheet sheet = loadSheetDataFrom(xssfsheet);
			if (sheet.getRows().isEmpty()) return afastamentos;
			afastamentos.setSheet(sheet);
		}
		return afastamentos;		
	}
	
	
	private AfastamentoSheet loadSheetDataFrom(XSSFSheet excelsheet) {
		AfastamentoSheet sheet = new AfastamentoSheet();
		DataFormatter df = new DataFormatter();
        Boolean endOfData = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
		Iterator<Row> rowItr = excelsheet.iterator();
        if (!rowItr.hasNext()) return sheet;
        Row row = goToInitialDataRow(rowItr);
        
        while (rowItr.hasNext() && !endOfData) {  // for each row
        	row = rowItr.next();
            AfastamentoRow afastRow = new AfastamentoRow();
                             
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {  // for each cell/column
                Cell cell = cellIterator.next();
                
                if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_DATA_INICIAL) {
                	String value = df.formatCellValue(cell);
                	
                	if (StringUtils.isEmpty(value)) break; // to next row if blank line                	
                	if (isEndOfData(value)) {
                		endOfData = true;
                		break;
                	}
                	
                	LocalDate date = formatter.parse(getDateSubstrFrom(value), LocalDate::from); 	
                	afastRow.setDataInicial(date);                	
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_DATA_FINAL) {
                	String value = df.formatCellValue(cell);
                	if (StringUtils.isNotEmpty(value)) {
                		LocalDate date = formatter.parse(getDateSubstrFrom(value), LocalDate::from); 	
                    	afastRow.setDataFinal(date);
                	}                	                	
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_DATA_RETORNO) {
                	String value = df.formatCellValue(cell); 
                	if (StringUtils.isNotEmpty(value)) {
                		LocalDate date = formatter.parse(getDateSubstrFrom(value), LocalDate::from); 	
                    	afastRow.setDataPrevistaRetorno(date);
                	}                	              	
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_PERIODO) {
                	String value = df.formatCellValue(cell);  
                	afastRow.setPeriodo(value);                	
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_MATRICULA) {
                	String value = df.formatCellValue(cell);  
                	FieldProcessor processor = new MatriculaFieldProcessor();
                	String matricula = processor.process(value);
                	afastRow.setMatricula(matricula);       
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_NOME) {
                	String value = df.formatCellValue(cell);  
                	afastRow.setNome(value);
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_MOTIVO) {
                	String value = df.formatCellValue(cell);  
                	afastRow.setMotivo(value);
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_TIPO) {
                	String value = df.formatCellValue(cell);  
                	afastRow.setTipo(value);  	
                }
            }
            
            if (!endOfData && afastRow.getDataInicial() != null) {
            	sheet.getRows().add(afastRow);
            }
        }
        
        return sheet;		
	}

	
	private Row goToInitialDataRow(Iterator<Row> rowItr) {
		Row row = rowItr.next();
        while ( rowItr.hasNext() && row.getRowNum() < INDEX_DATA_AFAST_INIT_ROW - 1 ) {
			row = rowItr.next();
		}
        return row;
	}
	
	private boolean isEndOfData(String value) {
		return value.startsWith("SIGRH / SC");
	}
	
	private String getDateSubstrFrom(String fullDateStr) {
		return fullDateStr.substring(0, 10);  // cell content format = '31/05/2018 00:00:00
	}


	public HistoricoAfastamentos getAfastamentos() {
		return afastamentos;
	}
	public void setAfastamentos(HistoricoAfastamentos afastamentos) {
		this.afastamentos = afastamentos;
	}
	public Path getFilePath() {
		return filePath;
	}
	public void setFilePath(Path filePath) {
		this.filePath = filePath;
	}
	
}
