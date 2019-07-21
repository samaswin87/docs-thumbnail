package com.docs.thumbnail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jodconverter.LocalConverter;
import org.jodconverter.LocalConverter.Builder;
import org.jodconverter.office.OfficeException;

public class ExcelThumbnailCreator implements ThumbnailCreator {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String FILE_PATTERN = "(?i).*\\.(xlsx|xls|XLS|XLSX)$";
	private static String TEMP = System.getProperty("java.io.tmpdir");

	@Override
	public void generate(String filePath, String thumbnailPath) {
		try {
			LOGGER.info("Read all Excel files into a list");
			List<String> files = FileReader.readFiles(filePath, FILE_PATTERN);

			files.forEach(file -> {

				File inputFile = new File(filePath + file);
				LOGGER.info("Read total pages from Excel file: " + file);
				int count = getPageCount(inputFile);
				File tempFile = new File(TEMP +inputFile.getName());
				try {
					int page = 0;
					while (count > 0) {
						LOGGER.info("Copy the file into temp location");	
						if (page == 0) {
							FileUtils.copyFile(inputFile, tempFile);
						}
						
						File input = tempFile;
						Builder builder = LocalConverter.builder();
						File outputFile = new File(thumbnailPath + inputFile.getName() + page + imagePrefix);
						builder.build().convert(input).to(outputFile).execute();
						removePage(tempFile);
						LOGGER.info("Processing page: " + page);
						page++;
						count--;
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
		if(file.getName().matches("(?i).*\\.(xlsx|XLSX)$")) {
			count = getXLSXPageCount(file);
		} else {
			count = getXLSPageCount(file);
		}
		return count;
	}
	
	public static void removePage(File source) throws IOException {
		if(source.getName().matches("(?i).*\\.(xlsx|XLSX)$")) {
			removeXLSXPage(source);
		} else {
			removeXLSPage(source);
		}
	}
	
	private int getXLSPageCount(File file) {
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		return workbook.getNumberOfSheets();
	}
	
	public static void removeXLSPage(File source) throws IOException {
		HSSFWorkbook workbook = null;
		try {
			workbook = new HSSFWorkbook(new FileInputStream(source));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		workbook.removeSheetAt(0);
		source.delete();
		FileOutputStream out = new FileOutputStream(new File(TEMP + source.getName() ));  
		workbook.write(out); 
	}
	
	private int getXLSXPageCount(File file) {
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(new FileInputStream(file));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		return workbook.getNumberOfSheets();
	}
	
	public static void removeXLSXPage(File source) throws IOException {
		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(new FileInputStream(source));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		}
		workbook.removeSheetAt(0);
		source.delete();
		FileOutputStream out = new FileOutputStream(new File(TEMP + source.getName() ));  
		workbook.write(out); 
	}
}
