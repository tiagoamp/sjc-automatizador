package com.tiagoamp.sjc.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;


public class PDFGenerator {
	
	private Document document = new Document();
	
	public void generateMessagesPdfFile(Map<String, List<ProcessingMessage>> messages, Path outputfile) throws FileNotFoundException, DocumentException {
		document.setPageSize(PageSize.A4.rotate());
		
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputfile.toFile()));
		writer.setPageEvent(new PdfGeneratorHeaderAndFooter("Relatório de Processamento"));
		
		setProperties("Relatório de Processamento");
		PdfPTable table = createTable(3);
		setPdfTableHeaders(table);
		
		Set<String> keys = messages.keySet();
		for (String sheetName : keys) {
			table = addRows(table, sheetName, messages.get(sheetName));	    	
		}
		
		document.add(table);		
		document.close();		
	}
	
	private void setProperties(String title) {
        document.addTitle(title);
        document.addSubject("SJC");
        document.addCreator("SJC-Automatizador");
        document.addAuthor("tiagoamp");
        document.addHeader("Expires", "0");
    }
        
    private PdfPTable createTable(int numberOfColumns) {
        document.open();
        PdfPTable table = new PdfPTable(numberOfColumns);
        table.setWidthPercentage(100);
        return table;
    }
    
    private void setPdfTableHeaders(PdfPTable table) {
        table.setHeaderRows(1);
        String[] headerRow = new String[]{"Planilha", "Tipo de Mensagem", "Mensagem"};
        for (String cabecalho : headerRow) {
            PdfPCell cell = new PdfPCell(new Paragraph(cabecalho));
            cell.setGrayFill(0.7f);
            table.addCell(cell);
        }
    }
    
    private PdfPTable addRows(PdfPTable table, String filename, List<ProcessingMessage> list) throws DocumentException {
    	Font font = new Font();
		font.setSize(14);
		
        for (ProcessingMessage msg : list) {
        	table.addCell(filename);
        	
        	Phrase pMsgType = null;
        	Phrase pMsgText = null;
        	if (msg.getType() == MessageType.ALERT) {
        		font.setColor(218, 165, 32);
        		pMsgType = new Phrase("ALERTA",font);        		
        		pMsgText = new Phrase(msg.getText(), font);        		
        	} else {
        		font.setColor(255, 55, 0);
        		pMsgType = new Phrase("ERRO",font);
        		pMsgText = new Phrase(msg.getText(), font);
        	}
        	table.addCell(pMsgType);
            table.addCell(pMsgText);
		}
    	return table;        
    }
        
}
