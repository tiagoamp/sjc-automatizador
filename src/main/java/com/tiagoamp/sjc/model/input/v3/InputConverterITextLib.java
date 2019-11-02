package com.tiagoamp.sjc.model.input.v3;

import static com.tiagoamp.sjc.model.SjcGeneralCode.ADMINISTRATIVO;
import static com.tiagoamp.sjc.model.SjcGeneralCode.OPERACIONAL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;
import com.tiagoamp.sjc.model.input.v3.to.IndexesPairTO;

public class InputConverterITextLib implements IInputConverter {
	
	private Path file;
	
	
	public InputConverterITextLib(Path file) {
		this.file = file;
	}
	
		
	public ConvertedSpreadsheet convert() throws IOException {
		PdfReader reader = new PdfReader(file.toString());
	    
		List<ConvertedSheet> convertedPages = new ArrayList<>();
		
		for (int pgNr=1; pgNr<=reader.getNumberOfPages(); pgNr++) {
			String pageContent = PdfTextExtractor.getTextFromPage(reader, pgNr);
			ConvertedSheet convertedPageSheet = loadDataFrom(pageContent);
			convertedPages.add(convertedPageSheet);
		}
		
		ConvertedSheet operacSheet = groupSheetPagesFor(OPERACIONAL, convertedPages);
		ConvertedSheet admSheet = groupSheetPagesFor(ADMINISTRATIVO, convertedPages);
		
		ConvertedSpreadsheet convertedSpreadsheet = new ConvertedSpreadsheet(operacSheet, admSheet, file);
		return convertedSpreadsheet;
	}
	
	
	private ConvertedSheet loadDataFrom(String content) {
		String nomeUnidade = null, monthStr = null, yearStr = null;
		SjcGeneralCode code = null;
		List<ConvRow> rows = new ArrayList<>();
		
		String[] lines = content.split("\n");
		
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			
			if (line.toUpperCase().matches("^MATRÍCULA NOME DO SERVIDOR HORA EXTRA.*")) {
				code = (line.toUpperCase().matches(".+PLANT(Ã|A)O.+")) ? OPERACIONAL : ADMINISTRATIVO;
				continue;
			}
				
			boolean shouldSkipLine = line.toUpperCase().matches("RELAT(Ó|O)RIO MENSAL DE HORA EXTRA, ADICIONAL NOTURNO E PLANT(Ã|A)O EXTRA") || 
					line.toUpperCase().matches("SECRETARIA DE ESTADO DA ADMINISTRA(Ç|C)(Ã|A)O PRISIONAL E SOCIOEDUCATIVA") || 
					line.toUpperCase().matches("NOME DA UNIDADE M(E|Ê)S ANO$") || 
					line.toUpperCase().matches("ADICIONAL") || line.toUpperCase().matches("NOTURNO"); 
			if (shouldSkipLine) continue;
			
			if (nomeUnidade == null) {
				String[] pgHeader = line.split(" ");
				if (pgHeader.length > 3) {
					yearStr = pgHeader[pgHeader.length-1];
					monthStr = pgHeader[pgHeader.length-2];
					nomeUnidade = line.replace(yearStr, "");
					nomeUnidade = line.replace(monthStr, "");	
				}				
			}
			
			if (line.matches("^\\D+.*"))  // non-numeric string start 
				continue;
			
			boolean isFirstRowOfAdm = rows.isEmpty() && code == ADMINISTRATIVO;
						
			String regexMatriculaAndNome = "^\\d+ \\D+.*";
			if (line.matches(regexMatriculaAndNome) || isFirstRowOfAdm) {
				ConvRow row = new ConvRow();
				String[] tokens = line.split(" ");
				int t = 0;
				row.setMatricula(tokens[t++]);
				String token = t <= tokens.length-1 ? tokens[t] : "";
				String nome = "";
//				if (isFirstRowOfAdm) {
//					nome = lines[i-1];
//				} else {							
					while (token.matches("\\D.*")) {
						nome += " " + token;
						if (t+1 >= tokens.length) break;
						token = tokens[++t];
					}	
//				}
				row.setNome(nome.trim());
				String[] dtPlantoesExtras = new String[5];
				int p = 0;
				if (t < tokens.length-1) {
					for (; t < tokens.length; t++) {
						token = tokens[t];
						dtPlantoesExtras[p++] = token;						
					}	
				}				
				String nextLine = i+1 <= lines.length-1 ? lines[i+1] : "";
				if (nextLine.matches("^\\d+( \\d+)*")) {
					i++;
					tokens = nextLine.split(" ");
					for (t = 0; t < tokens.length; t++) {
						if (t == 0) row.setQtdHoraExtra(tokens[t]);
						if (t == 1) row.setQtdAdicionalNoturno(tokens[t]);
						if (t == 2) dtPlantoesExtras[p++] = tokens[t];
					}					
				}
				row.setQtdPlantoesExtra(String.valueOf(p));
				row.setDtPlantoesExtras(dtPlantoesExtras);
				
				rows.add(row);
			} // for matricula and name line			
		} // for any line
		
		if (rows.isEmpty()) rows = null;
		ConvHeader header = createHeaderFrom(nomeUnidade, monthStr, yearStr);
		ConvertedSheet convSheet = new ConvertedSheet(code, header, rows);		
		return convSheet;		
	}
	
	private ConvHeader createHeaderFrom(String nomeUnidade, String month, String year) {
		Optional<YearMonth> yearMonth = MonthConverter.getYearMonthFrom(month, year);
		ConvHeader header = yearMonth.isPresent() ? new ConvHeader(yearMonth.get(), nomeUnidade) : new ConvHeader(nomeUnidade, month, year);
		return header;
	}
	
	private ConvertedSheet groupSheetPagesFor(SjcGeneralCode code, List<ConvertedSheet> convertedPages) {
		List<ConvertedSheet> convSheets = convertedPages.stream().filter(c -> c != null && c.getCode() == code).collect(Collectors.toList());
		if (convSheets == null) return null;
		
		List<ConvRow> rows = new ArrayList<>();
		ConvHeader header = new ConvHeader();
		
		for (ConvertedSheet convPage : convSheets) {
			ConvHeader pgHeader = convPage.getHeader();
			if (header.getNomeUnidadePrisional() == null) header.setNomeUnidadePrisional(pgHeader.getNomeUnidadePrisional());
			if (header.getYearMonthRef() == null) header.setYearMonthRef(pgHeader.getYearMonthRef());
			if (header.getMonthRefAsStr() == null) header.setMonthRefAsStr(pgHeader.getMonthRefAsStr());
			if (header.getYearRefAsStr() == null) header.setYearRefAsStr(pgHeader.getYearRefAsStr());
			if (convPage.getRows() != null) rows.addAll(convPage.getRows());
		}
		
		ConvertedSheet convSheet = new ConvertedSheet(code, header, rows);
		return convSheet;
	}
	
	
	public static void main(String[] args) throws IOException {
		InputConverterITextLib converter = 
				new InputConverterITextLib(Paths.get("/home/tiagoamp/PROJ/SJC/Arquivos prod/outubro 2019/CASA DO ALBERGADO.pdf"));
		ConvertedSpreadsheet s = converter.convert();
		Map<SjcGeneralCode, ConvertedSheet> convertedSheets = s.getConvertedSheets();
		List<ConvRow> rows = convertedSheets.get(OPERACIONAL).getRows();
		rows.forEach(System.out::println);
		System.out.println("---");
		rows = convertedSheets.get(ADMINISTRATIVO).getRows();
		rows.forEach(System.out::println);
	}
}
