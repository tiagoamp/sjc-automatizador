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

import org.eadge.extractpdfexcel.PdfConverter;
import org.eadge.extractpdfexcel.data.ExtractedData;
import org.eadge.extractpdfexcel.data.SortedData;
import org.eadge.extractpdfexcel.data.SortedPage;
import org.eadge.extractpdfexcel.data.XclPage;
import org.eadge.extractpdfexcel.data.block.Block;
import org.eadge.extractpdfexcel.data.lane.Lane;
import org.eadge.extractpdfexcel.exception.IncorrectFileTypeException;
import org.eadge.extractpdfexcel.models.TextBlockIdentifier;

import static com.tiagoamp.sjc.model.SjcGeneralCode.*;

import com.tiagoamp.sjc.model.SjcGeneralCode;
import com.tiagoamp.sjc.model.fieldprocessor.MonthConverter;
import com.tiagoamp.sjc.model.input.v3.InHeader;
import com.tiagoamp.sjc.model.input.v3.InRow;

public class InputConverter {
	
	private Path file;
	
	private final String FIELD_NOT_FOUND = "NAO ENCONTRADO";
	
	
	public InputConverter(Path file) {
		this.file = file;
	}

	
	public static void main(String[] args) throws FileNotFoundException, IncorrectFileTypeException {
		Path file = Paths.get("testfiles", "entrada", "Modelo.PDF");
		InputConverter c = new InputConverter(file);
		c.load();
	}
	
	/* https://github.com/eadgyo/Extract-PDF-Excel
	 */
	public void load() throws FileNotFoundException, IncorrectFileTypeException {
		
		ArrayList<XclPage> pages = PdfConverter.convertFileToXclPages(file.toAbsolutePath().toString());
		
		System.out.println(pages);
		
		ExtractedData extractedData = PdfConverter.extractFromFile(file.toAbsolutePath().toString(), new TextBlockIdentifier());
		SortedData sortedData = PdfConverter.sortExtractedData(extractedData);
		
		ArrayList<XclPage> pages1 = PdfConverter.convertFileToXclPages("/home/d333280/Ti/proj/workspace-proj/LeitorPdf/Modelo.PDF");
		
		
		
		
		//TODO: criar sheet de operacional e juntar as respectivas paginas
		//TODO: criar sheet de adm e juntar as respectivas paginas
		
		Map<Integer, SortedPage> pagesMap = sortedData.getPages();
		int qtPages = pagesMap.keySet().size();
		
		for (int pg = 1; pg <= qtPages; pg++) {
			
			SortedPage sortedPage = pagesMap.get(pg);
			
			XclPage excelPage = PdfConverter.createExcelPage(sortedPage);
			
			System.out.println("ueba");
			
			SjcGeneralCode code = identifySheetCode((sortedPage));
			InHeader headerRow = loadIdentificationInfo(sortedPage);						
			//List<InRow> personalRows = loadPersonalInfo(sortedPage);
			
		}			
		
	}
	
	private SjcGeneralCode identifySheetCode(SortedPage sortedPage) {
		Collection<Lane> lanes = sortedPage.getLines().getLanes();
		Iterator<Lane> iterator = lanes.iterator();
		final int rowsThreshold = 100;   // search at 'n' first lines 
		int counter = 0;
		
		while(iterator.hasNext() && counter < rowsThreshold) {
			Lane lane = iterator.next();
			
			TreeMap<Double, Block> blocks = lane.getBlocks();
			Collection<Block> values = blocks.values();
			for (Block block : values) {
				String text = block.getOriginalText().trim();				
				boolean hasPlantaoColumns = text.toUpperCase().matches("^PLANTÃO\\s\\d") || text.toUpperCase().matches("^PLANTAO\\s\\d") || 
						                    text.equalsIgnoreCase("TOTAL DE PLANTÕES EXTRAS");
				if (hasPlantaoColumns) return OPERACIONAL;
			}
			
		}
		return ADMINISTRATIVO;
	}
	
	private InHeader loadIdentificationInfo(SortedPage sortedPage) {
		Collection<Lane> lanes = sortedPage.getLines().getLanes();		
		Iterator<Lane> iterator = lanes.iterator();
		String nome = null, month = null, year = null;
		YearMonth yearMonth = null;
		int hits = 0, nrOfHeaderHits = 4, counter = 0, linesThreshold = 100;
		boolean isFieldsFilled = false;		
		
		while(iterator.hasNext() && counter < linesThreshold) {
			Lane lane = iterator.next();			
			if (isFieldsFilled) break;
			
			TreeMap<Double, Block> blocks = lane.getBlocks();
			Collection<Block> values = blocks.values();
			
			for (Block block : values) {
				String text = block.getOriginalText();
				
				if (hits != nrOfHeaderHits) {
					boolean isIndexHeader = text.toUpperCase().startsWith("NOME DA UNIDADE") ||  // hit 1 
			                (text.equalsIgnoreCase("MÊS") || text.equalsIgnoreCase("MES")) ||    // hit 2
			                text.equalsIgnoreCase("ANO") ||                                      // hit 3
			                text.equalsIgnoreCase("SJC");                                        // hit 4
					if (isIndexHeader) hits++;
					continue;
				}
				
				// nr of hits achieved
				if (nome == null) nome = text != null ? text : FIELD_NOT_FOUND;
				else if (month == null) month = text != null ? text : FIELD_NOT_FOUND;
				else if (year == null) year = text != null ? text : FIELD_NOT_FOUND;
				else {  // everything filled
					isFieldsFilled = true;
					break;  
				}
			}			
		}		
		
		Optional<Month> convertedMonth = MonthConverter.getConvertedMonth(month);
		if ( convertedMonth.isPresent() && year.matches("//d+")) {
			yearMonth = YearMonth.of(Integer.parseInt(year), convertedMonth.get());			
		}
		
		InHeader header = yearMonth != null ? new InHeader(yearMonth, nome) : new InHeader(nome, month, year);
		return header;
	}
	
	private List<InRow> loadPersonalInfo(SortedPage sortedPage, SjcGeneralCode code) {
		Collection<Lane> lanes = sortedPage.getLines().getLanes();		
		Iterator<Lane> iterator = lanes.iterator();
		List<InRow> rows = new ArrayList<>();
		boolean startInfo = false, endOfData = false; 
		
		
		
		final String textBeforeServidoresInfo = code == OPERACIONAL ? "TOTAL DE PLANTÕES EXTRAS" : "ADICIONAL NOTURNO";
		final String textAfterServidoresInfo = "TIPOS DE AFASTAMENTOS";
		
		while (iterator.hasNext() && !endOfData) {
			Lane lane = iterator.next();
			InRow row = startInfo ? new InRow() : null;;
			
			TreeMap<Double, Block> blocks = lane.getBlocks();
			Collection<Block> values = blocks.values();	
			for(Block block : values) {				
				String text = block.getOriginalText();
				
				if (!startInfo) {
					if (text.equalsIgnoreCase(textBeforeServidoresInfo)) {
						startInfo = true;
						break;
					}
					continue;
				}
				
				
												
			}			
		}
		
		return rows;
	}
	
}
