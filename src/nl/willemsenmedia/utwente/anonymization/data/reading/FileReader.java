package nl.willemsenmedia.utwente.anonymization.data.reading;

import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.FileType;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * The helper class that can read the supported file types and change them into data entries.
 */
public class FileReader {
	public static List<DataEntry> readFile(File chosenFile) {
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
				default:
					return new LinkedList<>();
			}
		}
		return null;
	}

	private static List<DataEntry> readCSVFile(File file) {
		return null;
	}

	private static List<DataEntry> readExcelFile(File file) {
		return null;
	}

	private static List<DataEntry> readXMLFile(File file) {
		LinkedList<DataEntry> list = new LinkedList<>();
//		list.add(new DataEntry(unstructuredText));
		return list;
	}

	private static FileType determineFileType(File file) {
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
			default:
				return null;
		}
	}
}
