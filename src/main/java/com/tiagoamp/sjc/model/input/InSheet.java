package com.tiagoamp.sjc.model.input;

import static com.tiagoamp.sjc.model.input.InputLayoutConstants.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.DataPlantaoFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.FieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.MatriculaFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.NumericFieldProcessor;

public class InSheet {
	
	public InSheet(SjcGeneralCode code) {
		this.code = code;
		this.messages = new ArrayList<>();
		this.inputrows = new ArrayList<>();
	}
	
	private SjcGeneralCode code;
	private List<InRow> inputrows;
	private List<ProcessingMessage> messages;
	private String monthRef;
	private String yearRef;
	
	
	@Override
	public String toString() {
		return String.format("Código: %s | Linhas: %d | Mensagens: %s", code, inputrows.size(), messages.size());		 
	}
	
	
	@SuppressWarnings("deprecation")
	public String loadCellValueFrom(XSSFSheet xssfsheet, CellAddress cellAddress) {
		Iterator<Row> rowItr = xssfsheet.iterator(); // Setting row
		if (!rowItr.hasNext()) return null;
		Row row = rowItr.next();
		while ( rowItr.hasNext() && row.getRowNum() < cellAddress.getRow() ) {
			row = rowItr.next();
		}
		
		Iterator<Cell> cellIterator = row.cellIterator(); // Setting column
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if ( cell.getAddress().compareTo(cellAddress) == 0 ) {
				if (CellType.STRING.compareTo(cell.getCellTypeEnum()) == 0) return cell.getStringCellValue();
				if (CellType.NUMERIC.compareTo(cell.getCellTypeEnum()) == 0) return String.valueOf(Integer.valueOf((int) cell.getNumericCellValue()));
			}					
		}
		return null;
	}
				
	public void loadDataFrom(XSSFSheet excelsheet) {
		Boolean endOfData = false;
		DataFormatter df = new DataFormatter();
		FieldProcessor fieldProcessor;
		
        Iterator<Row> rowItr = excelsheet.iterator();
        if (!rowItr.hasNext()) return;
        Row row = rowItr.next();
        while ( rowItr.hasNext() && row.getRowNum() < INDEX_DATA_INIT_ROW - 1 ) {
			row = rowItr.next();
		}
        
        while (rowItr.hasNext() && !endOfData) {  // for each row
        	row = rowItr.next();
            InRow inrow = new InRow();
            int qtdDatasPlantoes = 0;
                             
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {  // for each cell/column
                Cell cell = cellIterator.next();
                
                if (cell.getColumnIndex() == INDEX_COLUMN_MATRICULA) {
                	String value = df.formatCellValue(cell);
                	if (StringUtils.isEmpty(value)) break; // to next row
                	
                	if (isEndOfData(value)) {
                		endOfData = true;
                		break;
                	}
                	                	
                	if (hasNoNumbers(value)) {
                		messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém linha com dados inválidos (linha " + (row.getRowNum() + 1) + "). Processamento prosseguiu após estas linhas."));
                		break; // break column loop and go to next row loop                		
					}
                	
                	fieldProcessor = new MatriculaFieldProcessor();
                	inrow.setMatricula(fieldProcessor.process(value));
                	messages.addAll(fieldProcessor.getMessages());
                } else if (cell.getColumnIndex() == INDEX_COLUMN_NOME) {
                	if (StringUtils.isEmpty(df.formatCellValue(cell))) break; // next row
                	inrow.setNome(df.formatCellValue(cell));
                } else if (cell.getColumnIndex() == INDEX_COLUMN_HORA_EXTRA) {
                	fieldProcessor = new NumericFieldProcessor("Hora Extra");
                	inrow.setQtdHoraExtra(Integer.valueOf(fieldProcessor.process(df.formatCellValue(cell))));
                	messages.addAll(fieldProcessor.getMessages());
                } else if (cell.getColumnIndex() == INDEX_COLUMN_ADICIONAL_NOTURNO) {
                	fieldProcessor = new NumericFieldProcessor("Adicional Noturno");
                	inrow.setQtdAdicionalNoturno(Integer.valueOf(fieldProcessor.process(df.formatCellValue(cell))));
                	messages.addAll(fieldProcessor.getMessages());
                } else if (code == SjcGeneralCode.OPERACIONAL && cell.getColumnIndex() == INDEX_COLUMN_PLANTOES_EXTRAS) {
                	fieldProcessor = new NumericFieldProcessor("Plantão Extra");
                	inrow.setQtdPlantoesExtra(Integer.valueOf(fieldProcessor.process(df.formatCellValue(cell))));
                	messages.addAll(fieldProcessor.getMessages());
                	if (Integer.valueOf(inrow.getQtdPlantoesExtra()) != qtdDatasPlantoes) {
                		messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém na linha " + (row.getRowNum() + 1) +" 'Quantidade de Plantões Extras' diferente do número de datas. Cadastradas '" + qtdDatasPlantoes + "' datas, mas quantidade informada é " + inrow.getQtdPlantoesExtra() + "."));
                	}
                } else if ( (code == SjcGeneralCode.OPERACIONAL) && 
                		   (cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_01 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_02 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_03 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_04 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_05) 
                		   ) {
                	int indexPlantao = cell.getColumnIndex() - INDEX_COLUMN_PLANTOESEXTRAS_01; // getting index of 'plantao' from 0 to 4 (there may be 5 plantoes
                	String dataPlantaoExtra = df.formatCellValue(cell);
                	if (dataPlantaoExtra != null && !dataPlantaoExtra.isEmpty() && !hasNoNumbers(dataPlantaoExtra)) {                		
                		qtdDatasPlantoes++;                		
                		fieldProcessor = new DataPlantaoFieldProcessor(monthRef, yearRef);
                		inrow.getDtPlantoesExtras()[indexPlantao] = fieldProcessor.process(dataPlantaoExtra);
                	}
                }
                
            } 
            
            if (!endOfData && StringUtils.isNotEmpty(inrow.getNome())) {
            	this.inputrows.add(inrow);
            }
            
        }        
	}
	
	private boolean isEndOfData(String value) {
		return value.startsWith("Tipos de afastamentos");
	}

	private boolean hasNoNumbers(String value) {    	 
    	return !FieldProcessor.numericPattern.matcher(value).find();
    }
		
	public void print() {
		System.out.println(this.toString());
		for (InRow row : inputrows) {
			System.out.println(row.toString());
		}		
	}
		
	
	public List<InRow> getInputrows() {
		return inputrows;
	}
	public void setInputrows(List<InRow> inputrows) {
		this.inputrows = inputrows;
	}
	public SjcGeneralCode getCode() {
		return code;
	}
	public void setCode(SjcGeneralCode code) {
		this.code = code;
	}
	public List<ProcessingMessage> getMessages() {
		return messages;
	}
	public void setMessages(List<ProcessingMessage> messages) {
		this.messages = messages;
	}
	public String getMonthRef() {
		return monthRef;
	}
	public void setMonthRef(String monthRef) {
		this.monthRef = monthRef;
	}
	public String getYearRef() {
		return yearRef;
	}
	public void setYearRef(String yearRef) {
		this.yearRef = yearRef;
	}
		
}
