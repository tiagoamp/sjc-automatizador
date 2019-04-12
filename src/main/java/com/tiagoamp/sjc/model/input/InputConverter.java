package com.tiagoamp.sjc.model.input;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eadge.extractpdfexcel.PdfConverter;
import org.eadge.extractpdfexcel.data.ExtractedData;
import org.eadge.extractpdfexcel.data.SortedData;
import org.eadge.extractpdfexcel.data.SortedPage;
import org.eadge.extractpdfexcel.data.XclPage;
import org.eadge.extractpdfexcel.data.array.My2DArray;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.lane.Lane;
import org.eadge.extractpdfexcel.exception.IncorrectFileTypeException;
import org.eadge.extractpdfexcel.models.TextBlockIdentifier;

import static com.tiagoamp.sjc.model.SjcGeneralCode.*;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;
import com.tiagoamp.sjc.model.input.v3.ConvHeader;
import com.tiagoamp.sjc.model.input.v3.ConvRow;
import com.tiagoamp.sjc.model.input.v3.ConvertedSheet;
import com.tiagoamp.sjc.model.input.v3.to.IndexesPairTO;

public class InputConverter {
	
	private Path file;
	
	private final String FIELD_NOT_FOUND = "NAO ENCONTRADO";
	
	
	public InputConverter(Path file) {
		this.file = file;
	}
	
	
	
	/* https://github.com/eadgyo/Extract-PDF-Excel
	 */
	public void load() throws FileNotFoundException, IncorrectFileTypeException {
		// ArrayList<XclPage> pages = PdfConverter.convertFileToXclPages(file.toAbsolutePath().toString());  // gets pages as excel sheets style
		ExtractedData extractedData = PdfConverter.extractFromFile(file.toAbsolutePath().toString(), new TextBlockIdentifier());
		SortedData sortedData = PdfConverter.sortExtractedData(extractedData);
		
		Map<Integer, SortedPage> pagesMap = sortedData.getPages();
		Collection<SortedPage> pages = pagesMap.values();
		
		for (SortedPage page : pages) {
			My2DArray<Block> arrBlocks = page.create2DArrayOfBlocks();			
			
			ConvertedSheet convertedPageSheet = loadDataFrom(arrBlocks);
			
		}			
		
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
				
				if (indexes == null) { // initializes indexes before any other data
					if (!text.toUpperCase().matches("MATR(Í|I)CULA")) continue; 
					indexes = identifyServidoresDataIndexes(arr, col, line);
					if (indexes.getDataIndexes().isEmpty()) return null;  // no 'matriculas' found!!!					
				}
				
				if (text.toUpperCase().matches("MATR(Í|I)CULA")) {                                   
					List<String> matriculas = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					rows = generateRowsFromMatriculas(matriculas); // initializes rows
				} else if (text.toUpperCase().startsWith("NOME DA UNIDADE")) {
					Block nextBlock = arr.get(col, line+1);
					String value = nextBlock == null ? null : nextBlock.getOriginalText();
					nomeUnidade = value;
				} else if (text.toUpperCase().matches("M(E|Ê)S")) {
					Block nextBlock = arr.get(col, line+1);
					String value = nextBlock == null ? null : nextBlock.getOriginalText();
					monthStr = value;
				} else if (text.toUpperCase().matches("ANO")) {
					Block nextBlock = arr.get(col, line+1);
					String value = nextBlock == null ? null : nextBlock.getOriginalText();
					yearStr = value;
				} else if (text.toUpperCase().contains("NOME DO SERVIDOR")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).setNome( values.get(i) );
				} else if (text.toUpperCase().contains("HORA EXTRA")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).setQtdHoraExtra( values.get(i) );
				} else if (text.toUpperCase().contains("ADICIONAL NOTURNO")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).setQtdAdicionalNoturno( values.get(i) );
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 1")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[0] = values.get(i);
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 2")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[1] = values.get(i);
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 3")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[2] = values.get(i);
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 4")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[3] = values.get(i);
				} else if (text.toUpperCase().matches("PLANT(Ã|A)O 5")) {
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).getDtPlantoesExtras()[4] = values.get(i);
				} else if (text.toUpperCase().matches("TOTAL DE PLANT(Õ|O)ES EXTRAS")) {
					hasPlantoesFields = true;
					List<String> values = loadDataFromColumn(col, arr, indexes.getDataIndexes());
					for (int i = 0; i < rows.size(); i++) rows.get(i).setQtdPlantoesExtra( values.get(i) );
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
		return value != null && !value.isEmpty() && value.matches(".*\\d+.*") && !isStringOfEndOfFile(value);
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
		Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(month);
		YearMonth yearMonth = null;
		if ( convertedMonth.isPresent() && year.matches("//d+")) {
			yearMonth = YearMonth.of(Integer.parseInt(year), convertedMonth.get());			
		}		
		ConvHeader header = yearMonth != null ? new ConvHeader(yearMonth, nomeUnidade) : new ConvHeader(nomeUnidade, month, year);
		return header;
	}
		
		
	/*
	 * public static void main(String[] args) throws FileNotFoundException,
	 * IncorrectFileTypeException { Path file = Paths.get("testfiles", "entrada",
	 * "Modelo.PDF"); InputConverter c = new InputConverter(file); c.load(); }
	 */
	
}
