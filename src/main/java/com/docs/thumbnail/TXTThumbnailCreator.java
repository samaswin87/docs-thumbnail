package com.docs.thumbnail;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class TXTThumbnailCreator implements ThumbnailCreator{

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String FILE_PATTERN = "(?i).*\\.(txt|TXT)$";
	
	@Override
	public void generate(String filePath, String thumbnailPath) {
		try {
			LOGGER.info("Read all PDF files into a list");
			List<String> files = FileReader.readFiles(filePath, FILE_PATTERN);
			
			files.forEach(fileName -> {
				File inputFile = new File(filePath + fileName);
				File pdfFile = convertTextfileToPDF(filePath, inputFile);
				PDFThumbnailCreator.convertPDFToIMG(filePath, pdfFile.getName(), thumbnailPath);
				pdfFile.delete();
			});
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.toString());
		} 
	}

	public static File convertTextfileToPDF(String source, File file) {
		FileInputStream iStream = null;
		DataInputStream in = null;
		InputStreamReader is = null;
		BufferedReader br = null;
		File pdfFile = null;
		try {
			Document pdfDoc = new Document();
			pdfFile = new File(source, FilenameUtils.removeExtension(file.getName())+".pdf");
			PdfWriter.getInstance(pdfDoc, new FileOutputStream(pdfFile));
			pdfDoc.open();
			pdfDoc.setMarginMirroring(true);
			pdfDoc.setMargins(36, 72, 108, 180);
			pdfDoc.topMargin();
			Font normal_font = new Font();
			Font bold_font = new Font();
			bold_font.setStyle(Font.BOLD);
			bold_font.setSize(10);
			normal_font.setStyle(Font.NORMAL);
			normal_font.setSize(10);
			pdfDoc.add(new Paragraph());
			if (file.exists()) {
				iStream = new FileInputStream(file);
				in = new DataInputStream(iStream);
				is = new InputStreamReader(in);
				br = new BufferedReader(is);
				String strLine;
				while ((strLine = br.readLine()) != null) {
					Paragraph para = new Paragraph(strLine, normal_font);
					para.setAlignment(Element.ALIGN_JUSTIFIED);
					pdfDoc.add(para);
				}
			} else {
				LOGGER.info("file does not exist");
			}
			pdfDoc.close();
		}

		catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.toString());
		} finally {

			try {
				if (br != null) {
					br.close();
				}
				if (is != null) {
					is.close();
				}
				if (in != null) {
					in.close();
				}
				if (iStream != null) {
					iStream.close();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.toString());
			}

		}
		return pdfFile;
	}
}
