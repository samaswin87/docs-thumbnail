package com.docs.thumbnail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jodconverter.LocalConverter;
import org.jodconverter.LocalConverter.Builder;
import org.jodconverter.filter.text.PageSelectorFilter;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;

public class PDFThumbnailCreator implements ThumbnailCreator{
	
	private static final String FILE_PATTERN = "(?i).*\\.(pdf|PDF)$";
	
	@Override
	public void generate(String filePath, String thumbnailPath) {
		
		LocalOfficeManager officeManager = LocalOfficeManager.install();

		try {
			// Start an office process and connect to the started instance (on port 2002).
			officeManager.start();
			
			// Read all PDF files into a list
			List<String> files = FileReader.readFiles(filePath, FILE_PATTERN);
			
			// Get the system temp folder
			String tempFolder = System.getProperty("java.io.tmpdir");
			
			files.forEach(file -> {
				
				File inputFile = new File(filePath + file);
				// Total pages
				int count = getPageCount(inputFile);
				
				// Copy the file into temp location
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
						File outputFile = new File(thumbnailPath + inputFile.getAbsoluteFile().getName() + page + imagePrefix);
						builder.build().convert(input).to(outputFile).execute();
						System.out.println("Processing page: "+ page);
						removePage(tempFile);
						page ++;
					}
					
					// Finally remove the temp file
					tempFile.deleteOnExit();
				} catch (OfficeException | IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException | OfficeException e) {
			e.printStackTrace();
		} finally {
			try {
				officeManager.stop();
			} catch (OfficeException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	// Get PDF page count
	public static int getPageCount(File file) {
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} finally {
			try {
				doc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
