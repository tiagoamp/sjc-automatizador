package com.tiagoamp.sjc.model.input;

import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_DATA_FINAL;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_DATA_INICIAL;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_DATA_RETORNO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_MATRICULA;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_MOTIVO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_NOME;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_PERIODO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_AFAST_TIPO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_DATA_AFAST_INIT_ROW;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiagoamp.sjc.model.fieldprocessor.FieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.MatriculaFieldProcessor;

public class AfastamentosExcelSpreadsheet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AfastamentosExcelSpreadsheet.class);
	
	private HistoricoAfastamentos afastamentos;
	private Path filePath; 
	
	public static final String AFASTAMENTO_IDENTIFIED_FILE_NAME = "afastamento";
	public static final String NEW_AFASTAMENTO_IDENTIFIED_FILE_NAME = "AFASTAMENTOS";
	
	
	
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
		} catch (OLE2NotOfficeXmlFileException e) {
			String msg = "Arquivo de afastamentos não é um formato válido do MS-Office.";
			LOGGER.error(msg);
			throw new IOException(msg);
		}
		return afastamentos;		
	}
	
	private AfastamentoSheet loadSheetDataFrom(XSSFSheet excelsheet) {
		AfastamentoSheet sheet = new AfastamentoSheet();
		DataFormatter df = new DataFormatter();
        Boolean endOfData = false;
        
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
                	                	
                	LocalDate date = parseFromString(value);
                	afastRow.setDataInicial(date);                	
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_DATA_FINAL) {
                	String value = df.formatCellValue(cell);
                	if (StringUtils.isNotEmpty(value)) {
                		LocalDate date = parseFromString(value);
                    	afastRow.setDataFinal(date);
                	}                	                	
                } else if (cell.getColumnIndex() == INDEX_COLUMN_AFAST_DATA_RETORNO) {
                	String value = df.formatCellValue(cell); 
                	if (StringUtils.isNotEmpty(value)) {
                		LocalDate date = parseFromString(value);
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

	private LocalDate parseFromString(String value) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatter_eng = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter formatter_eng_short = DateTimeFormatter.ofPattern("M/dd/yy");
        DateTimeFormatter formatter_eng_short2 = DateTimeFormatter.ofPattern("M/d/yy");
        
		LocalDate date = null;
    	String valStr = getDateSubstrFrom(value);
    	try {
    		date = formatter.parse(valStr, LocalDate::from);
    	} catch (DateTimeParseException e) {
    		try {  // try american format
    			date = formatter_eng.parse(getDateSubstrFrom(valStr), LocalDate::from);
    		} catch (DateTimeParseException e2) {
    			try {
    				date = formatter_eng_short.parse(getDateSubstrFrom(valStr), LocalDate::from);
    			} catch (DateTimeParseException e3) {
    				date = formatter_eng_short2.parse(getDateSubstrFrom(valStr), LocalDate::from);
    			}
    		}                		
    	}
    	return date;
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
		String result = "";
		if (fullDateStr.length() >= 10) {
			result = fullDateStr.substring(0, 10);  // cell content format = '31/05/2018 00:00:00
		} else if (fullDateStr.length() < 10) {
			result = fullDateStr;
		}
		return result;
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
