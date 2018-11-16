package mw.extractsourcefromsdlxliff;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExtractSourceFromSDLXLIFF {

	public static void main(String[] args) {
		
		listAllFiles(Paths.get("./"), "*.sdlxliff");
		
	}
	
	public static List<Path> listAllFiles(Path directory, String acceptedFileNames) {
		List<Path> files = new ArrayList<>();
		
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory, acceptedFileNames)) {
			for (Path file : directoryStream) {
				files.add(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

}
