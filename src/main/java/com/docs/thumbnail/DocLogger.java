package com.docs.thumbnail;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DocLogger {
	static private FileHandler file;
	static private SimpleFormatter formatter;

	static public void init(String loggerName) throws IOException {

        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        logger.setLevel(Level.INFO);
        file = new FileHandler(loggerName+".log");
        formatter = new SimpleFormatter();
        file.setFormatter(formatter);
        logger.addHandler(file);;
    }
}
