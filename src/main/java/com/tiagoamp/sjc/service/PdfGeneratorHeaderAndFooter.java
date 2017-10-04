package com.tiagoamp.sjc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfGeneratorHeaderAndFooter extends PdfPageEventHelper{
	
	protected Paragraph header;
    protected PdfPTable footer;
    
    public PdfGeneratorHeaderAndFooter(String headerText) {
    	
    	Font fontHeader = new Font();
		fontHeader.setSize(14);
		fontHeader.setStyle(Font.BOLD);
		fontHeader.setStyle(Font.UNDERLINE);
		header = new Paragraph(headerText  + " [ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " hrs ]", fontHeader);
        
		Font fontFooter = new Font();
		fontFooter.setSize(12);
		fontFooter.setColor(139, 139, 131);
		footer = new PdfPTable(3);
        footer.setTotalWidth(580);
        footer.addCell(new Phrase(new Chunk("(c) 2017 tiagoamp",fontFooter)));
        footer.addCell(new Phrase(new Chunk("SJC-Automatizador",fontFooter)));
        footer.addCell(new Phrase(new Chunk("Data: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),fontFooter)));
    }
    
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, header,
            (document.right() - document.left()) / 2 + 78,
            document.top() + 10, 0);
        footer.writeSelectedRows(0, -1,
            (document.right() - document.left()) / 6 + 30,
            document.bottom() - 10, cb);
    }

}
