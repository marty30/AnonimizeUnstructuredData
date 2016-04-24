package nl.willemsenmedia.utwente.anonymization.data.handling;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.writing.FileWriter;
import nl.willemsenmedia.utwente.anonymization.gui.DataviewController;
import nl.willemsenmedia.utwente.anonymization.gui.ErrorHandler;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Martijn on 11-4-2016.
 */
public class AnonimizationController implements Callable<List<Callable<DataEntry>>> {
	private static AnonimizationController instance;
	private static AnonymizationTechnique definedTechnique;
	private final List<DataEntry> raw_data;
	private final DataEntry[] anonimous_data;
	private final ExecutorService threadpool;
	private final Settings settings;
	private final DataviewController dataviewController;
	private DoubleProperty progressProperty = new SimpleDoubleProperty();
	private boolean done = false;

	private AnonimizationController(List<DataEntry> raw_data, Settings settings, DataviewController dataviewController, @Nullable AnonymizationTechnique technique) {
		this.raw_data = raw_data;
		this.settings = settings;
		this.dataviewController = dataviewController;
		this.anonimous_data = new DataEntry[raw_data.size()];
		if (technique != null)
			setAnonyzationTechnique(technique);
		threadpool = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors() - 1, 1), r -> {
			Thread t = new Thread(r);
			t.setDaemon(true);
			return t;
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Performing some shutdown cleanup...");
				threadpool.shutdown();
				while (true) {
					try {
						System.out.println("Waiting for the service to terminate...");
						if (threadpool.awaitTermination(5, TimeUnit.SECONDS)) {
							break;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Done cleaning");
			}
		});
		if (!System.getProperty("useGUI").equals("false"))
			dataviewController.bind(progressProperty);
	}

	public static AnonymizationTechnique determineTechnique() {
		if (definedTechnique == null) {
			if (System.getProperty("technique") == null) {
				System.setProperty("technique", "");
			}
			switch (System.getProperty("technique")) {
				case "HashSentence":
					return new HashSentence();
				case "HashAll":
					return new HashAll();
				case "SmartHashing":
					return new SmartHashing();
				case "GeneralizeOrSuppress":
				case "k-anonymity":
					return new GeneralizeOrSuppress(Integer.parseInt(System.getProperty("k")));
				default:
					//Do nothing with the raw_data
					return new AnonymizationTechnique() {
						@Override
						public DataEntry anonymize(DataEntry dataEntry, List<DataEntry> raw_data, Settings settings) {
							DataEntry newDataEntry = new DataEntry(dataEntry.getHeaders());
							dataEntry.getDataAttributes().stream().forEach((dataAttribute) -> newDataEntry.addDataAttribute(dataAttribute.clone()));
							return newDataEntry;
						}
					};
			}
		}
		return definedTechnique;
	}

	/**
	 * This method can be used to set an anonymization technique that was not specified in @see{AnonimizationController#detemineTechnique()}
	 *
	 * @param technique The technique that should be used.
	 */
	public static void setAnonyzationTechnique(@Nonnull AnonymizationTechnique technique) {
		definedTechnique = technique;
	}

	public static AnonimizationController getInstance(List<DataEntry> raw_data, Settings settings, DataviewController dataviewController) {
		return getInstance(raw_data, settings, dataviewController, null);
	}

	public static AnonimizationController getInstance(List<DataEntry> raw_data, Settings settings, DataviewController dataviewController, @Nullable AnonymizationTechnique technique) {
		if (instance == null || !instance.raw_data.equals(raw_data) || !instance.settings.equals(settings) || !instance.dataviewController.equals(dataviewController) || !definedTechnique.equals(technique))
			instance = new AnonimizationController(raw_data, settings, dataviewController, technique);
		return instance;
	}

	public static void exportData() {
		if (!instance.done) {
			if (!System.getProperty("useGUI").equals("false"))
				//TODO ik weet nog niet zo goed wat ik hiermee ga doen.
				instance.call();
			else {
				while (!instance.done) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						ErrorHandler.handleException(e);
					}
				}
			}
		}
		FileWriter.exportDataToCSV(Arrays.asList(instance.anonimous_data), FileWriter.createFile(".csv"));
	}

	public DataEntry[] getAnonimousData() {
		return anonimous_data;
	}

	public List<DataEntry> getRawData() {
		return raw_data;
	}

	private void updateStatus(int index) {
		double workDone = index;
		double max = raw_data.size();
		if (!System.getProperty("useGUI").equals("false")) {
			// @see Task#updateProgress()
			// Adjust Infinity / NaN to be -1 for both workDone and max.
			if (Double.isInfinite(workDone) || Double.isNaN(workDone)) {
				workDone = -1;
			}

			if (Double.isInfinite(max) || Double.isNaN(max)) {
				max = -1;
			}

			if (workDone < 0) {
				workDone = -1;
			}

			if (max < 0) {
				max = -1;
			}

			// Clamp the workDone if necessary so as not to exceed max
			if (workDone > max) {
				workDone = max;
			}
			if (workDone == -1) {
				progressProperty.setValue(-1);
			} else {
				progressProperty.setValue(workDone / max);
			}
		}
		System.out.printf("\t- Entry " + (index + 1) + " van de " + raw_data.size() + " aan het verwerken. Dit is %.2f%% van het totaal.\r", (double) (index + 1) / raw_data.size() * 100);
		System.out.flush();

	}

	@Override
	public List<Callable<DataEntry>> call() {
		List<Callable<DataEntry>> preprocess_todolist = new ArrayList<>();
		for (DataEntry raw_entry : this.raw_data) {
			preprocess_todolist.add(() -> determineTechnique().doPreProcessing(raw_entry, settings));
			raw_entry.isPreProcessed = true;
		}

		List<Callable<DataEntry>> anonymize_todolist = new ArrayList<>();
		for (DataEntry raw_entry : this.raw_data) {
			anonymize_todolist.add(() -> {
				if (!raw_entry.isPreProcessed) {
					determineTechnique().doPreProcessing(raw_entry, settings);
				}
				int index = this.raw_data.indexOf(raw_entry);
				DataEntry anonimous_entry = determineTechnique().anonymize(raw_entry, raw_data, settings);
				anonimous_entry.isAnonymized = true;
				anonimous_data[index] = anonimous_entry;
				updateStatus(index);
				if (!System.getProperty("useGUI").equals("false"))
					pushEntryToView(raw_entry, anonimous_entry);
				return anonimous_entry;
			});
		}

		//Run all tasks
		System.out.println("Anonimization bezig ...");
		try {
			List<Future<DataEntry>> results_preprocess = threadpool.invokeAll(preprocess_todolist);
			List<Future<DataEntry>> results_anonymize = threadpool.invokeAll(anonymize_todolist);
		} catch (InterruptedException e) {
			ErrorHandler.handleException(e);
		}

		//Wait for everything to finish.
		while (!threadpool.isShutdown() || !threadpool.isTerminated()) {
			threadpool.shutdown();
		}
		updateStatus(raw_data.size());
		System.out.println("Anonimisatie is klaar!");
		this.done = true;
		return anonymize_todolist;
	}

	private void pushEntryToView(DataEntry raw_entry, DataEntry anonimous_entry) {
		Platform.runLater(() -> dataviewController.addData(raw_entry, anonimous_entry));
	}
}
