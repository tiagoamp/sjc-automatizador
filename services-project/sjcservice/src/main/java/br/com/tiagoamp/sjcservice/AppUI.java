package br.com.tiagoamp.sjcservice;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import br.com.tiagoamp.sjcservice.model.input.InputSpreadsheet;
import br.com.tiagoamp.sjcservice.model.output.OutputSpreadsheet;
import br.com.tiagoamp.sjcservice.service.SjcServicesFacade;

/**
 * User Interface by command line.
 *
 */
public class AppUI 
{
    public static void main( String[] args ) {
        
    	SjcServicesFacade service = new SjcServicesFacade();
    	
    	try {
    		
    		Path inputDirectory = Paths.get("/path/to/input/directory");
    		Path outputSpreadsheetFile = Paths.get("/path/to/output/spreadsheet.xlsx");
    		Path outputMessageFile = Paths.get("/path/to/output/messages.html");
    		
			List<InputSpreadsheet> inputSpreadsheetsFromDirectory = service.loadInputSpreadsheetsFromDirectory(inputDirectory);
			OutputSpreadsheet outputSpreadsheet = service.generateOutputSpreadSheet(inputSpreadsheetsFromDirectory);
			service.generateOutputMessagesFile(outputMessageFile, outputSpreadsheet);
			service.generateOuputSpreadsheetFile(outputSpreadsheetFile, outputSpreadsheet);
			
			System.out.println("Finished!");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
