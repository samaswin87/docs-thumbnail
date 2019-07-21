package com.docs.thumbnail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;

public class ThumbnailGenerator {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public enum DocType {
		WORD("WORD"), PDF("PDF"), PPT("PPT"), EXCEL("EXCEL"), TXT("TXT");
		
		private final String type;

		DocType(String type) {
	        this.type = type;
	    }
	    
	    public String value() {
	        return this.type;
	    }
	}
	
	public static void generate(String docType) {
		LocalOfficeManager officeManager = LocalOfficeManager.install();
		Properties prop = new Properties();

        try (InputStream input = ThumbnailGenerator.class.getResourceAsStream("/config.properties")) {
        	LOGGER.info("Starting the Libre Office...");
			// Start an office process and connect to the started instance (on port 2002).
			officeManager.start();
			prop.load(input);
			
			String filePath = prop.getProperty("docPath");
	        String thumbnailPath = prop.getProperty("thumnailPath");
	        ThumbnailCreator creator = null;
	        
	        if (docType.equals(DocType.PDF.value())) {
	        	LOGGER.info("Creating a PDF thumbnail..");
	        	creator = new PDFThumbnailCreator();
				creator.generate(filePath, thumbnailPath);
				
	        } else if (docType.equals(DocType.WORD.value())) {
	        	LOGGER.info("Creating a WORD thumbnail..");
	        	creator = new DocThubnailCreator();
				creator.generate(filePath, thumbnailPath);
				
	        } else if (docType.equals(DocType.PPT.value())) {
	        	LOGGER.info("Creating a PPT thumbnail..");
	        	creator = new PPTThumbnailCreator();
				creator.generate(filePath, thumbnailPath);
	        	
	        } else if (docType.equals(DocType.EXCEL.value())) {
	        	LOGGER.info("Creating a EXCEL thumbnail..");
	        	creator = new ExcelThumbnailCreator();
				creator.generate(filePath, thumbnailPath);
	        } else if (docType.equals(DocType.TXT.value())) {
	        	LOGGER.info("Creating a TXT thumbnail..");
	        	creator = new TXTThumbnailCreator();
				creator.generate(filePath, thumbnailPath);
	        }
			
		} catch (OfficeException | IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		} finally {
			try {
				officeManager.stop();
			} catch (OfficeException e) {
				LOGGER.log(Level.SEVERE, e.toString());
			}
		}
	}
}
