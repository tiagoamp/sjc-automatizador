package com.tiagoamp.sjc.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;

public class PDFGenerator {
	
	public void generateMessagesPdfFile(Map<String, List<ProcessingMessage>> messages, Path outputfile) throws FileNotFoundException, DocumentException {
		Document document = new Document();
		
		PdfWriter.getInstance(document, new FileOutputStream(outputfile.toFile()));
		
		document.open();

		document.addTitle("SJC - Relatorio de Processamento");
		document.addCreationDate();
		
		document.add(getHeaderParagraph());
		document.add(new Paragraph(""));
		document.add(new Paragraph(""));
				
		Set<String> keys = messages.keySet();
		for (String sheetName : keys) {
			document.add(getSheetNameParagraph(sheetName));
			document.add(new Paragraph(""));
			List<ProcessingMessage> sheetMessages = messages.get(sheetName);
			for (ProcessingMessage msg : sheetMessages) {
				if (msg.getType() == MessageType.ERROR) {
					document.add(getErrorMessageParagraph(msg.getText()));
				} else {
					document.add(getAlertMessageParagraph(msg.getText()));
				}
			}
			document.add(new Paragraph(""));
			document.add(new Paragraph(""));
		}
		
		document.add(new Paragraph(""));
		document.add(getFooterParagraph());
		
		document.close();		
	}
	
	
	private Paragraph getHeaderParagraph() {
		Font font = new Font();
		font.setSize(14);
		font.setStyle(Font.BOLD);
		font.setStyle(Font.UNDERLINE);
		
		LocalDate now  = LocalDate.now();
		Paragraph p = new Paragraph("SJC - Relatório de Processamento [" + now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "]",font);
		p.setAlignment(Element.ALIGN_CENTER);
		
		return p;				
	}
	
	private Paragraph getSheetNameParagraph(String sheetName) {
		Font font = new Font();
		font.setSize(12);
		font.setStyle(Font.BOLD);
		Paragraph p = new Paragraph(sheetName, font);
		return p;
	}
	
	private Paragraph getErrorMessageParagraph(String msgTxt) {
		Font font = new Font();
		font.setSize(12);
		font.setStyle(Font.ITALIC);
		font.setColor(BaseColor.RED);
		Paragraph p = new Paragraph(msgTxt, font);
		return p;
	}
	
	private Paragraph getAlertMessageParagraph(String msgTxt) {
		Font font = new Font();
		font.setSize(12);
		font.setStyle(Font.ITALIC);
		font.setColor(BaseColor.YELLOW);
		Paragraph p = new Paragraph(msgTxt, font);
		return p;
	}
	
	private Paragraph getFooterParagraph() {
		Font font = new Font();
		font.setSize(10);
		
		Paragraph p = new Paragraph("@tiagomp",font);
		p.setAlignment(Element.ALIGN_RIGHT);
		return p;				
	}
	
}
