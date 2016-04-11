package nl.willemsenmedia.utwente.anonymization.data.writing;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import nl.willemsenmedia.utwente.anonymization.gui.PopupManager;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Martijn on 4-4-2016.
 */
public class FileWriter {

	private static final String NEW_LINE_SEPARATOR = "\n";

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static File createFile(String ext) {
		Assert.assertTrue("Extensie moet beginnen met een punt!", ext.startsWith("."));
		String filename = "export_" + LocalDate.now().toString().trim().replace("\\s+", "_");
		File exportFile = null;
		try {
			exportFile = new File(filename + ext);
			if (!exportFile.exists())
				exportFile.createNewFile();
			else
				filename += "_" + LocalTime.now().toString().trim().replace("\\s+", "_").replace(":", "");
			exportFile = new File(filename + ext);
			if (!exportFile.exists())
				exportFile.createNewFile();
		} catch (IOException e) {
			ErrorHandler.handleException(e);
		}
		return exportFile;
	}

	public static void exportDataToTXT(List<DataEntry> data, File exportFile) {
		try (PrintWriter out = new PrintWriter(exportFile)) {
			for (DataEntry dataEntry : data)
				out.println(dataEntry.toString());
			PopupManager.info("Data geëxporteerd", null, "De data die hier zichtbaar is, is geëxporteerd naar het bestand met de naam \"" + exportFile.toString() + "\".");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleException(e);
		}
	}

	public static void exportDataToCSV(List<DataEntry> data, File exportFile) {
		List headers = data.get(0).getHeaders().stream().map(DataAttribute::getData).collect(Collectors.toList());


		java.io.FileWriter fileWriter = null;

		CSVPrinter csvFilePrinter = null;

		//Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

		try {

			//initialize FileWriter object
			fileWriter = new java.io.FileWriter(exportFile);

			//initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			//Create CSV file header
			csvFilePrinter.printRecord(headers);

			//Write a new student object list to the CSV file
			for (DataEntry dataEntry : data) {
				// new row
				List DataEntryRecord = dataEntry.getDataAttributes().stream().map(DataAttribute::getData).collect(Collectors.toList());
				csvFilePrinter.printRecord(DataEntryRecord);
			}

			System.out.println("CSV file was created successfully !!!");
			PopupManager.info("Data ge&euml;xporteerd", null, "De data die hier zichtbaar is, is geëxporteerd naar het bestand met de naam \"" + exportFile.toString() + "\".");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			ErrorHandler.handleException(e);
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
				if (csvFilePrinter != null)
					csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				ErrorHandler.handleException(e);
			}
		}
	}
}
