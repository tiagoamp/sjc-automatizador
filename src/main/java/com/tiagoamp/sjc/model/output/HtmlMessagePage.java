package com.tiagoamp.sjc.model.output;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.tiagoamp.sjc.model.MessageType;
import com.tiagoamp.sjc.model.ProcessingMessage;

public class HtmlMessagePage {
	
	public HtmlMessagePage() {		
	}
	
	public static final Path templateFile = Paths.get("src","main","resources","sjc","template_output.html");
	
		
	public String generate(Map<String, List<ProcessingMessage>> messages) throws IOException {
		Document doc = Jsoup.parse(templateFile.toFile(), "UTF-8", "");
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Element spanDatetimeElement = doc.getElementById("span-date-time");
		spanDatetimeElement.text(sdf.format(new Date()));
		
		Element fieldsetErrorElement = doc.getElementById("fieldset-error-msgs");
		Element fieldsetAlertElement = doc.getElementById("fieldset-alert-msgs");
		
		for (String filename : messages.keySet()) {
			Element fieldsetFileErrorElement = new Element(Tag.valueOf("fieldset"), "");
			fieldsetFileErrorElement.appendElement("legend").text(filename);
			Element fieldsetFileAlertElement = new Element(Tag.valueOf("fieldset"), "");
			fieldsetFileAlertElement.appendElement("legend").text(filename);
			
			Element ulListErrorElement = new Element(Tag.valueOf("ul"), "");
			Element ulListAlertElement = new Element(Tag.valueOf("ul"), "");
			
			for (ProcessingMessage msg : messages.get(filename)) {
				Element liElement = new Element(Tag.valueOf("li"), "").text(msg.getText());
				if (msg.getType() == MessageType.ERROR) {
					ulListErrorElement.appendChild(liElement);
				} else {
					ulListAlertElement.appendChild(liElement);
				}
			}
			
			if (!ulListErrorElement.getElementsByTag("li").isEmpty()) {
				fieldsetFileErrorElement.appendChild(ulListErrorElement);
				fieldsetErrorElement.appendChild(fieldsetFileErrorElement);
			}
			if (!ulListAlertElement.getElementsByTag("li").isEmpty()) {
				fieldsetFileAlertElement.appendChild(ulListAlertElement);
				fieldsetAlertElement.appendChild(fieldsetFileAlertElement);
			}			
		} 
		
		if (fieldsetErrorElement.getElementsByTag("ul").isEmpty()) fieldsetErrorElement.appendChild(getEmptyMessagesElement());
		if (fieldsetAlertElement.getElementsByTag("ul").isEmpty()) fieldsetAlertElement.appendChild(getEmptyMessagesElement());
			
		return doc.toString();
	}
	
	private Element getEmptyMessagesElement() {
		Element fieldsetElement = new Element(Tag.valueOf("fieldset"), "");
		fieldsetElement.appendElement("p").text("Não foram geradas mensagens.");
		return fieldsetElement;
	}
		
}
