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
		FileType fileType = determineFileType(chosenFile);
		int beginrij = Integer.parseInt(settings.getSettingsMap().get("beginrij") == null ? "0" : settings.getSettingsMap().get("beginrij").getValue());
		int eindrij = Integer.parseInt(settings.getSettingsMap().get("eindrij") == null ? "0" : settings.getSettingsMap().get("eindrij").getValue());
		boolean bevatKopteksten = Boolean.parseBoolean(settings.getSettingsMap().get("bevat_kopteksten") == null ? "false" : settings.getSettingsMap().get("bevat_kopteksten").getValue());
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
			Iterator<CSVRecord> iter = parser.iterator();
			//Handle the headers
			List<String> headers = new LinkedList<>();

				CSVRecord headerrow = iter.next();
				int i = 0;
				for (String csvAttribute : headerrow) {
					if (bevatKopteksten) {
						headers.add(i, csvAttribute);
					} else
						headers.add(i, "");
					i++;
				}

			int huidigerij = 0;
			CSVRecord csvRecord;
			while (iter.hasNext()) {
				csvRecord = iter.next();
				if (huidigerij >= beginrij && (huidigerij <= eindrij || eindrij <= 0)) {
					DataEntry dataEntry = new DataEntry();

					int c = 0;
					for (String csvAttribute : csvRecord) {
						dataEntry.addDataAttribute(new DataAttribute(DataType.UNSTRUCTURED, headers.get(c), csvAttribute));
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
				for (int i = 0; i < cols; i++) {
					cell = sheet.getRow(0).getCell(i);
					if (cell != null) {
						if (bevatKopteksten) {
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
						} else {
							headers.add(i, "");
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
