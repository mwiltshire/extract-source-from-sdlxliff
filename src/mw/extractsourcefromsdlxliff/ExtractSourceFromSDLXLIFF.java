package mw.extractsourcefromsdlxliff;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.codec.binary.Base64;

public class ExtractSourceFromSDLXLIFF {

	public static void main(String[] args) {

		for (Path sdlxliff : listAllFiles(Paths.get("./"), "*.sdlxliff")) {

			try {

				String base64String = findBase64EncodedSourceInSDLXLIFF(sdlxliff.toString());
				extractSource(decode(base64String));

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

	public static String findBase64EncodedSourceInSDLXLIFF(String file)
			throws FileNotFoundException, XMLStreamException, IOException {

		StringBuffer stringBuffer = new StringBuffer();

		try (FileInputStream fileInputStream = new FileInputStream(file)) {

			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(fileInputStream);

			Boolean lastEventWasReferenceElement = false;

			while (reader.hasNext()) {

				int event = reader.next();

				if (event == XMLStreamReader.START_ELEMENT && reader.getLocalName() == "internal-file") {
					lastEventWasReferenceElement = true;
				}

				if (lastEventWasReferenceElement && event == XMLStreamReader.CHARACTERS) {
					stringBuffer.append(reader.getText());
				}

				if (lastEventWasReferenceElement && event == XMLStreamReader.END_ELEMENT)
					break;

			}

			fileInputStream.close();
			reader.close();
		}

		return stringBuffer.toString();

	}

	public static byte[] decode(String base64EncodedString) {

		return Base64.decodeBase64(base64EncodedString);

	}

	public static void extractSource(byte[] decodedBase64) {
		ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(decodedBase64));
		ZipEntry entry = null;

		try {

			while ((entry = zipStream.getNextEntry()) != null) {
				String entryName = entry.getName();

				String outputFile = "./" + entryName;

				if (Files.notExists(Paths.get(outputFile))) {
					FileOutputStream out = null;

					try {
						out = new FileOutputStream(outputFile, false);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					byte[] byteBuffer = new byte[4096];
					int bytesRead = 0;

					while ((bytesRead = zipStream.read(byteBuffer)) != -1)
						out.write(byteBuffer, 0, bytesRead);

					out.close();
					zipStream.closeEntry();

				} else {

					throw new IOException("File already exists in this directory");

				}
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());

		}

		try {

			zipStream.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
