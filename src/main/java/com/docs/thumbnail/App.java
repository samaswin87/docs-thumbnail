package com.docs.thumbnail;

import java.io.IOException;
import java.util.logging.Logger;


public class App 
{
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
    public static void main(String[] args) throws IOException
    {
    	DocLogger.init("doc_thumbnail");
    	ThumbnailGenerator.generate();
    }
}
