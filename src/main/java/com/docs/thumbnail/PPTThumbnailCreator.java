package com.docs.thumbnail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.jodconverter.LocalConverter;
import org.jodconverter.LocalConverter.Builder;
import org.jodconverter.office.OfficeException;

public class PPTThumbnailCreator implements ThumbnailCreator {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String FILE_PATTERN = "(?i).*\\.(ppt|PPT|pptx|PPTX)$";
	private static String TEMP = System.getProperty("java.io.tmpdir");

	@Override
	public void generate(String filePath, String thumbnailPath) {
		try {
			LOGGER.info("Read all PPT files into a list");
			List<String> files = FileReader.readFiles(filePath, FILE_PATTERN);
			
			files.forEach(file -> {

				File inputFile = new File(filePath + file);

				
				LOGGER.info("Read total pages from PPT file: " + file);
				int count = 0;
				count = getPageCount(inputFile);
				try {
					int page = 1;
					File tempFile = new File(TEMP +inputFile.getName());
					while (count > 0) {
						LOGGER.info("Copy the file into temp location");	
						if (page == 1) {
							FileUtils.copyFile(inputFile, tempFile);
						}
						count--;
						File input = tempFile;
						Builder builder = LocalConverter.builder();
						File outputFile = new File(thumbnailPath + inputFile.getName() + page + imagePrefix);
				 		builder.build().convert(input).to(outputFile).execute();
						removePage(tempFile);
						LOGGER.info("Processing page: " + page);
						page++;
					}
				} catch (OfficeException | IOException e) {
					e.printStackTrace();
					LOGGER.log(Level.SEVERE, e.toString());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.toString());
		}
	}
	
	private int getPageCount(File file) {
		int count = 0;
		if(file.getName().matches("(?i).*\\.(pptx|PPTX)$")) {
			count = getPPTXPageCount(file);
		} else {
			count = getPPTPageCount(file);
		}
		return count;
	}
	
	public static void removePage(File source) throws IOException {
		if(source.getName().matches("(?i).*\\.(pptx|PPTX)$")) {
			removePPTXPage(source);
		} else {
			removePPTPage(source);
		}
	}
	
	private int getPPTXPageCount(File file) {
		XMLSlideShow xslideShow = null;
		try {
			xslideShow = new XMLSlideShow(OPCPackage.open(file));
		} catch (OpenXML4JException e) {
			e.printStackTrace();
		}
		return xslideShow.getSlides().size();
	}
	
	public static void removePPTXPage(File source) throws IOException {
		XMLSlideShow xslideShow = null;
		try {
			xslideShow = new XMLSlideShow(OPCPackage.open(source));
		} catch (OpenXML4JException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		xslideShow.removeSlide(0);
		source.delete();
		FileOutputStream out = new FileOutputStream(new File(TEMP + source.getName() ));  
		xslideShow.write(out); 
	}
	
	private int getPPTPageCount(File file) {
		HSLFSlideShow document = null;
		try {
			document = new HSLFSlideShow(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document.getSlides().size();
	}
	
	public static void removePPTPage(File source) throws IOException {
		HSLFSlideShow document = null;
		try {
			document = new HSLFSlideShow(new FileInputStream(source));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		document.removeSlide(0);
		source.delete();
		FileOutputStream out = new FileOutputStream(new File(TEMP + source.getName() ));  
		document.write(out); 
	}

}
