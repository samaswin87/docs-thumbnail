package com.docs.thumbnail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ThumbnailGenerator {
	
	public static void generate() {
		
		Properties prop = new Properties();
        try (InputStream input = ThumbnailGenerator.class.getResourceAsStream("/config.properties")) {
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		
        String filePath = prop.getProperty("docPath");
        String thumbnailPath = prop.getProperty("thumnailPath");
		ThumbnailCreator t = new PDFThumbnailCreator();
		t.generate(filePath, thumbnailPath);
		
		t = new DocThubnailCreator();
		t.generate(filePath, thumbnailPath);
		
		
		t = new PPTThumbnailCreator();
		t.generate(filePath, thumbnailPath);
		
		t = new ExcelThumbnailCreator();
		t.generate(filePath, thumbnailPath);
	}
}
