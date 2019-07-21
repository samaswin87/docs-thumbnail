package com.docs.thumbnail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jodconverter.LocalConverter;
import org.jodconverter.LocalConverter.Builder;
import org.jodconverter.office.OfficeException;

public class PDFThumbnailCreator implements ThumbnailCreator{
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String FILE_PATTERN = "(?i).*\\.(pdf|PDF)$";
	
	@Override
	public void generate(String filePath, String thumbnailPath) {
		try {
			LOGGER.info("Read all PDF files into a list");
			List<String> files = FileReader.readFiles(filePath, FILE_PATTERN);
			
			files.forEach(fileName -> {
				convertPDFToIMG(filePath, fileName, thumbnailPath);
			});
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		} 
	}
	
	public static void convertPDFToIMG(String filePath, String fileName, String thumbnailPath) {
		LOGGER.info("Get the system temp folder");
		String tempFolder = System.getProperty("java.io.tmpdir");
		
		File inputFile = new File(filePath + fileName);
		LOGGER.info("Read total pages from PDF file: "+ fileName);
		int count = getPageCount(inputFile);
		
		LOGGER.info("Copy the file into temp location");
		File tempFile = new File(tempFolder + inputFile.getName());
		
		try {
			FileUtils.copyFile(inputFile, tempFile);
			
			/* 
			 * Start the page count from 0.
			 * Take the temp file as a input file
			 * Create a thumbnail. It will create for first page
			 * Remove the first page from temp file and save   
			 * */ 
			int page = 0;
			
			while(count > 0) {
				count --;		
				File input = tempFile;
				Builder builder = LocalConverter.builder();
				File outputFile = new File(thumbnailPath + inputFile.getName() + page + imagePrefix);
				builder.build().convert(input).to(outputFile).execute();
				LOGGER.info("Processing page: "+ page);
				removePage(tempFile);
				page ++;
			}
			
			LOGGER.info("Finally remove the temp file");
			tempFile.deleteOnExit();
		} catch (OfficeException | IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
	}
	
	
	// Get PDF page count
	public static int getPageCount(File file) {
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		return doc.getNumberOfPages();
	}
	
	// Remove the PDF file 0 page 
	public static void removePage(File source) {
		PDDocument doc = null;
		try {
			doc = PDDocument.load(source);
			doc.removePage(0);
			doc.save(source);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		} finally {
			try {
				doc.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.toString());
			}
		}
	}
}
