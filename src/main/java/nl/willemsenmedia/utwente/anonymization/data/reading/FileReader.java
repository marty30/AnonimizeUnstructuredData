package nl.willemsenmedia.utwente.anonymization.data.reading;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.FileType;
import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * The helper class that can read the supported file types and change them into data entries.
 */
public class FileReader {
	public static List<DataEntry> readFile(File chosenFile, Settings settings) {
		if (chosenFile.isFile() && chosenFile.exists()) {
			FileType fileType = determineFileType(chosenFile);
			int beginrij = Integer.parseInt(settings.getSettingsMap().get("beginrij") == null ? "0" : settings.getSettingsMap().get("beginrij").getValue());
			int eindrij = Integer.parseInt(settings.getSettingsMap().get("eindrij") == null ? "0" : settings.getSettingsMap().get("eindrij").getValue());
			boolean bevatKopteksten = Boolean.parseBoolean(settings.getSettingsMap().get("bevat_kopteksten") == null ? "false" : settings.getSettingsMap().get("bevat_kopteksten").getValue());
			List<DataAttribute> headers;
			if (fileType != null) {
				switch (fileType) {
					case XLS:
					case XLSX:
						if (bevatKopteksten)
							headers = readExcelHeaders(chosenFile);
						else
							headers = new ArrayList<>(readExcelHeaders(chosenFile).size());
						return readExcelFile(chosenFile, headers, beginrij, eindrij);
					case CSV:
						if (bevatKopteksten)
							headers = readCSVHeaders(chosenFile);
						else
							headers = new ArrayList<>(readCSVHeaders(chosenFile).size());
						return readCSVFile(chosenFile, headers, beginrij, eindrij);
					case XML:
						return readXMLFile(chosenFile);
					case TXT:
					default:
						return readTXTFile(chosenFile);
				}
			}
		} else if (chosenFile.isDirectory()) {
			System.err.println(chosenFile.getAbsolutePath() + " is a directory. You should have used DirReader.readDir() instead of read file.");
			return DirReader.readDir(chosenFile, settings);
		}
		System.err.println("Could not handle the following file: " + chosenFile.getAbsolutePath());
		return null;
	}

	public static List<DataAttribute> readCSVHeaders(File file) {
		try {
			CSVParser parser = CSVParser.parse(getContentFromFile(file), CSVFormat.EXCEL);
			Iterator<CSVRecord> iter = parser.iterator();
			//Handle the headers
			List<DataAttribute> headers = new LinkedList<>();

			CSVRecord headerrow = iter.next();
			int i = 0;
			for (String csvAttribute : headerrow) {
				headers.add(i, new DataAttribute(DataType.UNSTRUCTURED, csvAttribute));
				i++;
			}
			return headers;
		} catch (IOException e) {
			ErrorHandler.handleException(e);
			return null;
		}
	}

	private static List<DataEntry> readCSVFile(File file, List<DataAttribute> headers, int beginrij, int eindrij) {
		List<DataEntry> data = new ArrayList<>();
		CSVParser parser;
		try {
			parser = CSVParser.parse(getContentFromFile(file), CSVFormat.EXCEL);
			Iterator<CSVRecord> iter = parser.iterator();
			int huidigerij = 0;
			CSVRecord csvRecord;
			while (iter.hasNext()) {
				csvRecord = iter.next();
				if (huidigerij >= beginrij && (huidigerij <= eindrij || eindrij <= 0)) {
					DataEntry dataEntry = new DataEntry(headers);

					int c = 0;
					for (String csvAttribute : csvRecord) {
						dataEntry.addDataAttribute(new DataAttribute(headers.get(c).getDataType(), headers.get(c).getData(), csvAttribute, headers.get(c).doAnonimize()));
						c++;
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

	public static List<DataAttribute> readExcelHeaders(File file) {
		try {
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheetAt(0);
			Row row;
			Cell cell;

			int cols = 0; // No of columns
			int tmp;
			int rows = sheet.getLastRowNum();

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for (int i = 0; i < 10 || i < rows; i++) {
				row = sheet.getRow(i);
				if (row != null) {
					tmp = sheet.getRow(i).getPhysicalNumberOfCells();
					if (tmp > cols) cols = tmp;
				}
			}

			//Handle the headers
			List<DataAttribute> headers = new LinkedList<>();
			for (int i = 0; i < cols; i++) {
				cell = sheet.getRow(0).getCell(i);
				if (cell != null) {
					switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							headers.add(i, new DataAttribute(DataType.UNSTRUCTURED, cell.getStringCellValue()));
							break;
						case Cell.CELL_TYPE_NUMERIC:
							headers.add(i, new DataAttribute(DataType.UNSTRUCTURED, cell.getNumericCellValue() + ""));
							break;
						case Cell.CELL_TYPE_FORMULA:
							headers.add(i, new DataAttribute(DataType.UNSTRUCTURED, cell.getCellFormula()));
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							headers.add(i, new DataAttribute(DataType.UNSTRUCTURED, cell.getBooleanCellValue() + ""));
							break;
						case Cell.CELL_TYPE_ERROR:
							headers.add(i, new DataAttribute(DataType.UNSTRUCTURED, Byte.toString(cell.getErrorCellValue())));
							break;
						case Cell.CELL_TYPE_BLANK:
						default:
							headers.add(i, new DataAttribute(DataType.UNSTRUCTURED, ""));
							break;
					}
				}
			}
			return headers;
		} catch (IOException | InvalidFormatException e) {
			ErrorHandler.handleException(e);
			return null;
		}
	}

	private static List<DataEntry> readExcelFile(File file, List<DataAttribute> headers, int beginrij, int eindrij) {
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



			List<DataEntry> data = new ArrayList<>();
			for (int r = beginrij; r < rows; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					DataEntry dataEntry = new DataEntry(headers);

					for (int c = 0; c < cols; c++) {
						cell = row.getCell(c);
						if (cell != null) {
							switch (cell.getCellType()) {
								case Cell.CELL_TYPE_STRING:
									dataEntry.addDataAttribute(new DataAttribute(headers.get(c).getDataType(), headers.get(c).getData(), cell.getStringCellValue(), headers.get(c).doAnonimize()));
									break;
								case Cell.CELL_TYPE_NUMERIC:
									dataEntry.addDataAttribute(new DataAttribute(headers.get(c).getDataType(), headers.get(c).getData(), cell.getNumericCellValue() + "", headers.get(c).doAnonimize()));
									break;
								case Cell.CELL_TYPE_FORMULA:
									dataEntry.addDataAttribute(new DataAttribute(headers.get(c).getDataType(), headers.get(c).getData(), cell.getCellFormula(), headers.get(c).doAnonimize()));
									break;
								case Cell.CELL_TYPE_BOOLEAN:
									dataEntry.addDataAttribute(new DataAttribute(headers.get(c).getDataType(), headers.get(c).getData(), cell.getBooleanCellValue() + "", headers.get(c).doAnonimize()));
									break;
								case Cell.CELL_TYPE_ERROR:
									dataEntry.addDataAttribute(new DataAttribute(headers.get(c).getDataType(), headers.get(c).getData(), Byte.toString(cell.getErrorCellValue()), headers.get(c).doAnonimize()));
									break;
								case Cell.CELL_TYPE_BLANK:
								default:
									dataEntry.addDataAttribute(new DataAttribute(headers.get(c).getDataType(), headers.get(c).getData(), "", headers.get(c).doAnonimize()));
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
			list.add(new DataEntry(null, new DataAttribute(DataType.UNSTRUCTURED, getContentFromFile(file))));
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
