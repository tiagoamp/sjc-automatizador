package com.tiagoamp.sjc.model.input.v3;

import static com.tiagoamp.sjc.model.SjcGeneralCode.ADMINISTRATIVO;
import static com.tiagoamp.sjc.model.SjcGeneralCode.OPERACIONAL;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eadge.extractpdfexcel.PdfConverter;
import org.eadge.extractpdfexcel.data.ExtractedData;
import org.eadge.extractpdfexcel.data.SortedData;
import org.eadge.extractpdfexcel.data.SortedPage;
import org.eadge.extractpdfexcel.data.array.My2DArray;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.exception.IncorrectFileTypeException;
import org.eadge.extractpdfexcel.models.TextBlockIdentifier;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;
import com.tiagoamp.sjc.model.input.v3.to.IndexesPairTO;

public class InputConverterNew {
	
	private Path file;
	
	private final String AUTHENTICATION_TEXT_START = "PARA VERIFICAR A AUTENTICIDADE";
	
	
	public InputConverterNew(Path file) {
		this.file = file;
	}
	
		
	public ConvertedSpreadsheet convert() throws FileNotFoundException, IncorrectFileTypeException {
		// https://github.com/eadgyo/Extract-PDF-Excel
		// ArrayList<XclPage> pages = PdfConverter.convertFileToXclPages(file.toAbsolutePath().toString());  // gets pages as excel sheets style
		ExtractedData extractedData = PdfConverter.extractFromFile(file.toAbsolutePath().toString(), new TextBlockIdentifier());
		SortedData sortedData = PdfConverter.sortExtractedData(extractedData);
		
		Map<Integer, SortedPage> pagesMap = sortedData.getPages();
		if (pagesMap == null || pagesMap.size() == 0) return null;
		Collection<SortedPage> pages = pagesMap.values();
		
		List<ConvertedSheet> convertedPages = pages.stream()
				.map(page -> {
					My2DArray<Block> arrBlocks = page.create2DArrayOfBlocks();
					ConvertedSheet convertedPageSheet = loadDataFrom(arrBlocks);
					return convertedPageSheet;
				}).collect(Collectors.toList());
		
		
		ConvertedSheet operacSheet = groupSheetPagesFor(OPERACIONAL, convertedPages);
		ConvertedSheet admSheet = groupSheetPagesFor(ADMINISTRATIVO, convertedPages);
		
		ConvertedSpreadsheet convertedSpreadsheet = new ConvertedSpreadsheet(operacSheet, admSheet, file);
		return convertedSpreadsheet;		
	}
	
	private ConvertedSheet loadDataFrom(My2DArray<Block> arr) {
		int nrOfCols = arr.numberOfColumns(), nrOfRows = arr.numberOfLines();
		String nomeUnidade = null, monthStr = null, yearStr = null;
		List<ConvRow> rows = null;
		boolean hasPlantoesFields = false;
				
		IndexesPairTO indexes = null;
		
		for(int col=0; col < nrOfCols; col++) {
			for(int line=0; line < nrOfRows; line++) {
				Block block = arr.get(col, line);
				if (block == null) continue;
				String text = block.getOriginalText().trim();
				
				boolean shouldSkipColumn = text.toUpperCase().startsWith(AUTHENTICATION_TEXT_START);  
				if (shouldSkipColumn) break;
				
				if (indexes == null && text.toUpperCase().matches("MATR(Í|I)CULA")) { // initializes indexes 
					indexes = identifyServidoresDataIndexes(arr, col, line);
					if (indexes.getDataIndexes().isEmpty()) return null;  // no 'matriculas' found!!!
					List<String> matriculas = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					rows = generateRowsFromMatriculas(matriculas); // initializes rows
					// load servidores names
					List<String> values = loadDataFromColumn((col+1), arr, indexes.getDataIndexes());
					if (values.get(0) != null && !values.get(0).isEmpty()) 
						for (int i = 0; i < rows.size(); i++) rows.get(i).setNome( values.get(i) );
					break;
				}
				
				if (text.toUpperCase().startsWith("NOME DA UNIDADE")) {
					Block nextBlock = arr.get(col, line+1);
					String value = nextBlock == null ? null : nextBlock.getOriginalText();
					if (value != null) nomeUnidade = value.toUpperCase();
					//break;  // at 'adm' spreadsheet the 'nome servidor' is the same column of 'nome unidade' 
				} else if (text.toUpperCase().matches("M(E|Ê)S")) {
					Block nextBlock = arr.get(col, line+1);
					String value = nextBlock == null ? null : nextBlock.getOriginalText();
					monthStr = value;
				} else if (text.toUpperCase().matches("ANO")) {
					Block nextBlock = arr.get(col, line+1);
					String value = nextBlock == null ? null : nextBlock.getOriginalText();
					yearStr = value;
				}
				
				if (indexes == null) continue;
				
				if (text.toUpperCase().contains("NOME DO SERVIDOR")) {
					if (rows.get(0).getNome() != null) break; // already loaded
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).setNome( values.get(i) );
					break;
				} else if (text.toUpperCase().startsWith("HORA EXTRA")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).setQtdHoraExtra( values.get(i) );
					break;
				} else if (text.toUpperCase().startsWith("ADICIONAL NOTURNO")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).setQtdAdicionalNoturno( values.get(i) );
					break;
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 1")) {
					hasPlantoesFields = true;
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());  
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[0] = values.get(i);
					break;
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 2")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[1] = values.get(i);
					break;
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 3")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[2] = values.get(i);
					break;
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 4")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[3] = values.get(i);
					break;
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 5")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[4] = values.get(i);
					break;
				} 							
			}
		}
		
		ConvHeader header = createHeaderFrom(nomeUnidade, monthStr, yearStr);
		SjcGeneralCode code = hasPlantoesFields ? OPERACIONAL : ADMINISTRATIVO;
		ConvertedSheet convSheet = new ConvertedSheet(code, header, rows);		
		return convSheet;		
	}
		
	private IndexesPairTO identifyServidoresDataIndexes(My2DArray<Block> arr, final Integer col, final Integer line) {
		String possibleMatricula = null;
		boolean foundValidMatricula = false, hasMatriculaToScan = true;
		Integer servidoresInitIndex = null, servidoresEndIndex = null;
		int nrOfLines = arr.numberOfLines(), currLine = line, qtMatr = 0;
		List<Integer> indexes = new ArrayList<>();
		
		while (hasMatriculaToScan) {
			Block nextBlock = arr.get(col, ++currLine);
			if (nextBlock != null) {
				possibleMatricula = nextBlock.getOriginalText();
				foundValidMatricula = isValidMatricula(possibleMatricula);
				if (servidoresInitIndex == null && foundValidMatricula) servidoresInitIndex = currLine;
				servidoresEndIndex = servidoresInitIndex != null ? (servidoresInitIndex + qtMatr) : null;
				if (foundValidMatricula) {
					qtMatr++;
					indexes.add(currLine);
				}
			}
			hasMatriculaToScan = (currLine+1) < nrOfLines && !isStringOfEndOfFile(possibleMatricula);
		}
		
		IndexesPairTO pairTO = new IndexesPairTO(servidoresInitIndex, servidoresEndIndex, indexes);
		return pairTO;
	}
	
	private boolean isValidMatricula(String value) {
		return value != null && !value.isEmpty() && value.trim().matches("^\\d+.*") && !isStringOfEndOfFile(value);
	}
	
	private boolean isStringOfEndOfFile(String value) {
		return value != null && value.toUpperCase().startsWith("TIPOS DE AFASTAMENTOS");
	}
	
	private List<ConvRow> generateRowsFromMatriculas(List<String> matriculas) {
		return matriculas.stream().map(matr -> {
			ConvRow row = new ConvRow();
			row.setMatricula(matr);
			return row;
		}).collect(Collectors.toList());
	}
	
	private List<String> loadDataFromColumn(final Integer col, My2DArray<Block> arr, List<Integer> dataIndexes) {
		List<String> values = dataIndexes.stream()
				.map(index -> {
					Block nextBlock = arr.get(col, index);
					String value = nextBlock == null ? null : nextBlock.getOriginalText();
					return value;
				}).collect(Collectors.toList());
		return values;
	}
	
	private ConvHeader createHeaderFrom(String nomeUnidade, String month, String year) {
		Optional<YearMonth> yearMonth = MonthConverter.getYearMonthFrom(month, year);
		ConvHeader header = yearMonth.isPresent() ? new ConvHeader(yearMonth.get(), nomeUnidade) : new ConvHeader(nomeUnidade, month, year);
		return header;
	}
	
	private ConvertedSheet groupSheetPagesFor(SjcGeneralCode code, List<ConvertedSheet> convertedPages) {
		List<ConvertedSheet> convSheets = convertedPages.stream().filter(c -> c.getCode() == code).collect(Collectors.toList());
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
		
		
	/*
	 * public static void main(String[] args) throws FileNotFoundException,
	 * IncorrectFileTypeException { Path file = Paths.get("testfiles", "entrada",
	 * "Modelo.PDF"); InputConverter c = new InputConverter(file); c.load(); }
	 */
	
}
