package nl.willemsenmedia.utwente.anonymization.data.reading;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.FileType;
import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * The helper class that can read the supported file types and change them into data entries.
 */
public class FileReader {
	public static List<DataEntry> readFile(File chosenFile, HashMap<String, Node> additionalOptions) {
		FileType fileType = determineFileType(chosenFile);
		int beginrij = 0;
		Node beginrijNode = additionalOptions.get("beginrij");
		if (beginrijNode != null)
			beginrij = Integer.parseInt(((TextField) beginrijNode).getText() == null ? "0" : "0" + ((TextField) beginrijNode).getText());
		int eindrij = 0;
		Node eindrijNode = additionalOptions.get("eindrij");
		if (eindrijNode != null)
			eindrij = Integer.parseInt(((TextField) eindrijNode).getText() == null ? "0" : "0" + ((TextField) eindrijNode).getText());
		boolean bevatKopteksten = false;
		Node koptekstenNode = additionalOptions.get("bevat_kopteksten");
		if (koptekstenNode != null) {
			bevatKopteksten = ((CheckBox) koptekstenNode).isSelected();
		}
		if (fileType != null) {
			switch (fileType) {
				case XLS:
				case XLSX:
					return readExcelFile(chosenFile, bevatKopteksten, beginrij, eindrij);
				case CSV:
					return readCSVFile(chosenFile, bevatKopteksten, beginrij, eindrij);
				case XML:
					return readXMLFile(chosenFile);
				case TXT:
				default:
					return readTXTFile(chosenFile);
			}
		}
		return null;
	}

	private static List<DataEntry> readCSVFile(File file, boolean bevatKopteksten, int beginrij, int eindrij) {
		List<DataEntry> data = new ArrayList<>();
		CSVParser parser;
		try {
			parser = CSVParser.parse(getContentFromFile(file), CSVFormat.EXCEL);
			int huidigerij = 0;
			for (CSVRecord csvRecord : parser) {
				if (huidigerij >= beginrij && huidigerij <= eindrij) {
					DataEntry dataEntry = new DataEntry();

					for (String csvAttribute : csvRecord) {
						dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, csvAttribute));
					}
					data.add(dataEntry);
				}
				huidigerij++;
			}
		} catch (IOException e) {
			ErrorHandler.handleException(e);
		}
		return data;
	}

	private static List<DataEntry> readExcelFile(File file, boolean bevatKopteksten, int beginrij, int eindrij) {
		try {
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheetAt(0);
			Row row;
			Cell cell;

			int rows; // No of rows
			if (eindrij <= 0)
				eindrij = sheet.getLastRowNum();
			rows = Math.min(sheet.getLastRowNum(), eindrij);

			int cols = 0; // No of columns
			int tmp;

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for (int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols) cols = tmp;
				}
			}

			//Handle the headers
			List<String> headers = new LinkedList<>();
			if (bevatKopteksten) {
				for (int i = 0; i < cols; i++) {
					cell = sheet.getRow(0).getCell(i);
					if (cell != null) {
						switch (cell.getCellType()) {
							case Cell.CELL_TYPE_STRING:
								headers.add(i, cell.getStringCellValue());
								break;
							case Cell.CELL_TYPE_NUMERIC:
								headers.add(i, cell.getNumericCellValue() + "");
								break;
							case Cell.CELL_TYPE_FORMULA:
								headers.add(i, cell.getCellFormula());
								break;
							case Cell.CELL_TYPE_BOOLEAN:
								headers.add(i, cell.getBooleanCellValue() + "");
								break;
							case Cell.CELL_TYPE_ERROR:
								headers.add(i, Byte.toString(cell.getErrorCellValue()));
								break;
							case Cell.CELL_TYPE_BLANK:
							default:
								headers.add(i, "");
								break;
						}
					}
				}
			}

			List<DataEntry> data = new ArrayList<>();
			for (int r = beginrij; r < rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					DataEntry dataEntry = new DataEntry();

					for (int c = 0; c < cols; c++) {
						cell = row.getCell(c);
						if (cell != null) {
							switch (cell.getCellType()) {
								case Cell.CELL_TYPE_STRING:
									dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, headers.get(c), cell.getStringCellValue()));
									break;
								case Cell.CELL_TYPE_NUMERIC:
									dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, headers.get(c), cell.getNumericCellValue() + ""));
									break;
								case Cell.CELL_TYPE_FORMULA:
									dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, headers.get(c), cell.getCellFormula()));
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, headers.get(c), cell.getBooleanCellValue() + ""));
									break;
								case Cell.CELL_TYPE_ERROR:
									dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, headers.get(c), Byte.toString(cell.getErrorCellValue())));
									break;
								case Cell.CELL_TYPE_BLANK:
								default:
									dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, headers.get(c), ""));
									break;
							}
						}
					}
					data.add(dataEntry);
				}
			}
			return data;
		} catch (Exception ioe) {
			ErrorHandler.handleException(ioe);
			return new ArrayList<>();
		}
	}

	private static List<DataEntry> readXMLFile(File file) {
		return readTXTFile(file);
	}

	private static List<DataEntry> readTXTFile(File file) {
		LinkedList<DataEntry> list = new LinkedList<>();
		try {
			list.add(new DataEntry(new DataAttribute(DataType.UNSTRUCTURED, getContentFromFile(file))));
		} catch (IOException e) {
			ErrorHandler.handleException(e);
		}
		return list;
	}

	private static String getContentFromFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new java.io.FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
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
