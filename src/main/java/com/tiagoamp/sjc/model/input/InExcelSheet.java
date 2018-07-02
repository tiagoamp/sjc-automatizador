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

import java.time.DateTimeException;
import java.time.YearMonth;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.DataPlantaoFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.FieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.MatriculaFieldProcessor;
import com.tiagoamp.sjc.model.fieldprocessor.NumericFieldProcessor;

public class InExcelSheet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InExcelSheet.class);
	
	private InSheet sheet;
	private SjcGeneralCode code;
	private YearMonth yearMonthRef;
	private DataFormatter df = new DataFormatter();
	
	
	public InExcelSheet(SjcGeneralCode code, YearMonth yearMonthRef) {
		this.code = code;
		this.yearMonthRef = yearMonthRef;
	}
	
	public InSheet loadDataFrom(XSSFSheet excelsheet) {
		sheet = new InSheet(code, yearMonthRef);				
				
		Boolean endOfData = false;
		FieldProcessor fieldProcessor;
		
        Iterator<Row> rowItr = excelsheet.iterator();
        if (!rowItr.hasNext()) return sheet;
        Row row = goToInitialDataRow(rowItr);
        
        while (rowItr.hasNext() && !endOfData) {  // for each row
        	row = rowItr.next();
            InRow inrow = new InRow();
            int qtdDatasPlantoesPreenchidas = 0;
                             
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {  // for each cell/column
                Cell cell = cellIterator.next();
                
                if (cell.getColumnIndex() == INDEX_COLUMN_MATRICULA) {
                	String value = df.formatCellValue(cell);
                	
                	if (StringUtils.isEmpty(value)) break; // to next row if blank line                	
                	if (isEndOfData(value)) {
                		endOfData = true;
                		break;
                	}                	                	
                	if (hasNoNumbers(value)) {
                		sheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Planilha contém linha com dados inválidos (linha " + (row.getRowNum() + 1) + "). Processamento prosseguiu após estas linhas."));
                		break; // break column loop and go to next row loop                		
					}
                	
                	fieldProcessor = new MatriculaFieldProcessor();
                	inrow.setMatricula(fieldProcessor.process(value));
                	sheet.getMessages().addAll(fieldProcessor.getMessages());
                } else if (cell.getColumnIndex() == INDEX_COLUMN_NOME) {
                	if (StringUtils.isEmpty(df.formatCellValue(cell))) break; // next row if blank line
                	inrow.setNome(df.formatCellValue(cell));
                } else if (cell.getColumnIndex() == INDEX_COLUMN_HORA_EXTRA) {
                	fieldProcessor = new NumericFieldProcessor("Hora Extra");
                	inrow.setQtdHoraExtra(Integer.valueOf(fieldProcessor.process(df.formatCellValue(cell))));
                	sheet.getMessages().addAll(fieldProcessor.getMessages());
                } else if (cell.getColumnIndex() == INDEX_COLUMN_ADICIONAL_NOTURNO) {
                	fieldProcessor = new NumericFieldProcessor("Adicional Noturno");
                	inrow.setQtdAdicionalNoturno(Integer.valueOf(fieldProcessor.process(df.formatCellValue(cell))));
                	sheet.getMessages().addAll(fieldProcessor.getMessages());
                } else if (code == SjcGeneralCode.OPERACIONAL && cell.getColumnIndex() == INDEX_COLUMN_PLANTOES_EXTRAS) {
                	fieldProcessor = new NumericFieldProcessor("Plantão Extra");
                	inrow.setQtdPlantoesExtra(Integer.valueOf(fieldProcessor.process(df.formatCellValue(cell))));
                	sheet.getMessages().addAll(fieldProcessor.getMessages());
                	if (Integer.valueOf(inrow.getQtdPlantoesExtra()) != qtdDatasPlantoesPreenchidas) {
                		sheet.getMessages().add(new ProcessingMessage(MessageType.ALERT, "Planilha contém na linha " + (row.getRowNum() + 1) +" 'Quantidade de Plantões Extras' diferente do número de datas. Cadastradas '" + qtdDatasPlantoesPreenchidas + "' datas, mas quantidade informada é " + inrow.getQtdPlantoesExtra() + "."));
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
                		try {
                			fieldProcessor = new DataPlantaoFieldProcessor(yearMonthRef);
                    		inrow.getDtPlantoesExtras()[indexPlantao] = fieldProcessor.process(dataPlantaoExtra);
                		} catch (DateTimeException e) {
                			LOGGER.debug("Padrão de data não reconhecido: " + dataPlantaoExtra);
                			sheet.getMessages().add(new ProcessingMessage(MessageType.ERROR, "Planilha contém na linha " + (row.getRowNum() + 1) +" "
                					+ "'Data de Plantão Extra' com formato não reconhecido: '" + dataPlantaoExtra + "'. Formato recomendado = 'dd/mm/aaaa'."));
                			inrow.getDtPlantoesExtras()[indexPlantao] = dataPlantaoExtra;
                		}
                		                		                		
                	}
                }
                
            } 
            
            if (!endOfData && StringUtils.isNotEmpty(inrow.getNome())) {
            	sheet.getRows().add(inrow);
            }            
        }
        return sheet;
	}
	
		
	private Row goToInitialDataRow(Iterator<Row> rowItr) {
		Row row = rowItr.next();
        while ( rowItr.hasNext() && row.getRowNum() < INDEX_DATA_INIT_ROW - 1 ) {
			row = rowItr.next();
		}
        return row;
	}
	
	private boolean isEndOfData(String value) {
		return value.startsWith("Tipos de afastamentos") || value.startsWith("Este relatório deverá ser encaminhado");
	}

	private boolean hasNoNumbers(String value) {    	 
    	return !FieldProcessor.numericPattern.matcher(value).find();
    }

}
