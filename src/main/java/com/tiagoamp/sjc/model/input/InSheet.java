package com.tiagoamp.sjc.model.input;

import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_ADICIONAL_NOTURNO;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_HORA_EXTRA;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_MATRICULA;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_NOME;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_PLANTOESEXTRAS_01;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_PLANTOESEXTRAS_02;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_PLANTOESEXTRAS_03;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_PLANTOESEXTRAS_04;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_PLANTOESEXTRAS_05;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_COLUMN_PLANTOES_EXTRAS;
import static com.tiagoamp.sjc.model.input.InputLayoutConstants.INDEX_DATA_INIT_ROW;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.DataPlantaoFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.FieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.MatriculaFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.NumericFieldProcessor;

public class InSheet {
	
	private SjcGeneralCode code;
	private YearMonth yearMonthRef;
	private List<InRow> rows;
	private List<ProcessingMessage> messages;
		
	
	public InSheet(SjcGeneralCode code) {
		this.code = code;
		this.messages = new ArrayList<>();
		this.rows = new ArrayList<>();
	}
	
	public InSheet(SjcGeneralCode code, YearMonth yearMonthRef) {
		this(code);
		this.yearMonthRef = yearMonthRef;
	}
	
	@Override
	public boolean equals(Object obj) {
		return ((obj instanceof InSheet)) && (((InSheet)obj).getCode() == this.code) ;		
	}
	
	@Override
	public int hashCode() {
		return this.code.getCode();
	}
	
	@Override
	public String toString() {
		return String.format("Código: %s | Linhas: %d | Mensagens: %s", code, rows.size(), messages.size());		 
	}
	
	public void print() {
		System.out.println(this.toString());
		rows.forEach(System.out::println);				
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
            int qtdDatasPlantoesPreenchidas = 0;
                             
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
                	if (Integer.valueOf(inrow.getQtdPlantoesExtra()) != qtdDatasPlantoesPreenchidas) {
                		messages.add(new ProcessingMessage(MessageType.ALERT, "Planilha contém na linha " + (row.getRowNum() + 1) +" 'Quantidade de Plantões Extras' diferente do número de datas. Cadastradas '" + qtdDatasPlantoesPreenchidas + "' datas, mas quantidade informada é " + inrow.getQtdPlantoesExtra() + "."));
                	}
                } else if ( (code == SjcGeneralCode.OPERACIONAL) && 
                		   (cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_01 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_02 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_03 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_04 || 
                		    cell.getColumnIndex() == INDEX_COLUMN_PLANTOESEXTRAS_05) 
                		   ) {
                	int indexPlantao = cell.getColumnIndex() - INDEX_COLUMN_PLANTOESEXTRAS_01; // getting index of 'plantao' from 0 to 4 (there may be 5 plantoes)
                	String dataPlantaoExtra = df.formatCellValue(cell);
                	if (dataPlantaoExtra != null && !dataPlantaoExtra.isEmpty() && !hasNoNumbers(dataPlantaoExtra)) {                		
                		qtdDatasPlantoesPreenchidas++;                		
                		fieldProcessor = new DataPlantaoFieldProcessor(yearMonthRef);
                		inrow.getDtPlantoesExtras()[indexPlantao] = fieldProcessor.process(dataPlantaoExtra);
                	}
                }
                
            } 
            
            if (!endOfData && StringUtils.isNotEmpty(inrow.getNome())) {
            	this.rows.add(inrow);
            }
            
        }        
	}
	
	private boolean isEndOfData(String value) {
		return value.startsWith("Tipos de afastamentos");
	}

	private boolean hasNoNumbers(String value) {    	 
    	return !FieldProcessor.numericPattern.matcher(value).find();
    }
		
	
	public List<InRow> getRows() {
		return rows;
	}
	public void setRows(List<InRow> inputrows) {
		this.rows = inputrows;
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
	public YearMonth getYearMonthRef() {
		return yearMonthRef;
	}
	public void setYearMonthRef(YearMonth yearMonthRef) {
		this.yearMonthRef = yearMonthRef;
	}
		
}
