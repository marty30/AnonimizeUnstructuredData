package nl.willemsenmedia.utwente.anonymization.data.reading;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.FileType;
import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * The helper class that can read the supported file types and change them into data entries.
 */
public class FileReader {
	public static List<DataEntry> readFile(File chosenFile, ObservableList<Node> childrenUnmodifiable) {
		FileType fileType = determineFileType(chosenFile);
		if (fileType != null) {
			switch (fileType) {
				case XLS:
				case XLSX:
					return readExcelFile(chosenFile);
				case CSV:
					return readCSVFile(chosenFile);
				case XML:
					return readXMLFile(chosenFile);
				case TXT:
				default:
					return readTXTFile(chosenFile);
			}
		}
		return null;
	}

	private static List<DataEntry> readCSVFile(File file) {
		List<DataEntry> data = new ArrayList<>();
		CSVParser parser;
		try {
			parser = CSVParser.parse(getContentFromFile(file), CSVFormat.EXCEL);

			for (CSVRecord csvRecord : parser) {
				DataEntry dataEntry = new DataEntry();

				for (String csvAttribute : csvRecord) {
					dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, csvAttribute));
				}
				data.add(dataEntry);
			}
		} catch (IOException e) {
			ErrorHandler.handleException(e);
		}
		return data;
	}

	private static List<DataEntry> readExcelFile(File file) {
		return null;
	}

	private static List<DataEntry> readXMLFile(File file) {
		return readTXTFile(file);
	}

	private static List<DataEntry> readTXTFile(File file) {
		LinkedList<DataEntry> list = new LinkedList<>();
		list.add(new DataEntry(new DataAttribute(DataType.UNSTRUCTURED, getContentFromFile(file))));
		return list;
	}

	private static String getContentFromFile(File file) {
		try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		} catch (IOException e) {
			ErrorHandler.handleException(e);
			return null;
		}
	}

	public static FileType determineFileType(File file) {
		String ext = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")).toLowerCase();
		switch (ext) {
			case ".xlsx":
				return FileType.XLSX;
			case ".xls":
				return FileType.XLS;
			case ".csv":
				return FileType.CSV;
			case ".xml":
				return FileType.XML;
			case ".txt":
				return FileType.TXT;
			default:
				return FileType.ONBEKEND;
		}
	}
}
