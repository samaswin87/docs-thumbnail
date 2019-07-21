package com.docs.thumbnail;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jodconverter.LocalConverter;
import org.jodconverter.LocalConverter.Builder;
import org.jodconverter.filter.text.PageCounterFilter;
import org.jodconverter.office.OfficeException;

public class DocThubnailCreator implements ThumbnailCreator {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static final String FILE_PATTERN = "(?i).*\\.(doc|docx|DOC|DOCX)$";

	@Override
	public void generate(String filePath, String thumbnailPath) {
		try {
			LOGGER.info("Read all Word files into a list");
			List<String> files = FileReader.readFiles(filePath, FILE_PATTERN);

			files.forEach(file -> {

				File inputFile = new File(filePath + file);
				LOGGER.info("Read total pages from Word file: " + file);
				int count = 1;
				try {
					int page = 1;

					while (count > 0) {
						count--;
						Builder builder = LocalConverter.builder();
						File outputFile = new File(thumbnailPath + inputFile.getName() + page + imagePrefix);
						PageCounterFilter pageCountfilter = new PageCounterFilter();
						CustomPageSelectorFilter pageSelectfilters = new CustomPageSelectorFilter(page);
						builder = builder.filterChain(pageSelectfilters);
						if (page == 1) {
							builder.filterChain(pageCountfilter).build().convert(inputFile).to(outputFile).execute();
							count = pageCountfilter.getPageCount() - 1;
						} else {
							builder.build().convert(inputFile).to(outputFile).execute();
						}
						LOGGER.info("Processing page: " + page);
						page++;
					}
				} catch (OfficeException e) {
					e.printStackTrace();
					LOGGER.log(Level.SEVERE, e.toString());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.toString());
		}
	}
}
