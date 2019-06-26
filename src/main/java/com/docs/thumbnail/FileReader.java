package com.docs.thumbnail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReader {
	
	static public List<String> readFiles(String filePath, String pefix) throws IOException {
        final File file = Paths.get(filePath).toFile();
        List<String>  collectedFiles = Stream.of(file.list()).filter(f -> f.matches(pefix)).collect(Collectors.toList());
        return collectedFiles;
	}
}
