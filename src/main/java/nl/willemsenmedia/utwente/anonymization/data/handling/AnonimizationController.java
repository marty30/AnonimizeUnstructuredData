package nl.willemsenmedia.utwente.anonymization.data.handling;

import javafx.application.Platform;
import javafx.concurrent.Task;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.writing.FileWriter;
import nl.willemsenmedia.utwente.anonymization.gui.DataviewController;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Martijn on 11-4-2016.
 */
public class AnonimizationController extends Task<List<Task<DataEntry>>> {
	private static AnonimizationController instance;
	private final List<DataEntry> raw_data;
	private final DataEntry[] anonimous_data;
	private final ExecutorService threadpool;
	private final Settings settings;
	private final DataviewController dataviewController;

	private AnonimizationController(List<DataEntry> raw_data, Settings settings, DataviewController dataviewController) {
		this.raw_data = raw_data;
		this.settings = settings;
		this.dataviewController = dataviewController;
		this.anonimous_data = new DataEntry[raw_data.size()];
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
		dataviewController.bind(this);
	}

	public static AnonymizationTechnique determineTechnique() {
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
					public DataEntry anonymize(DataEntry dataEntry, Settings settings) {
						DataEntry newDataEntry = new DataEntry(dataEntry.getHeaders());
						dataEntry.getDataAttributes().stream().forEach((dataAttribute) -> newDataEntry.addDataAttribute(dataAttribute.clone()));
						return newDataEntry;
					}
				};
		}
	}

	public static AnonimizationController getInstance(List<DataEntry> raw_data, Settings settings, DataviewController dataviewController) {
		if (instance == null || !instance.raw_data.equals(raw_data) || !instance.settings.equals(settings) || !instance.dataviewController.equals(dataviewController))
			instance = new AnonimizationController(raw_data, settings, dataviewController);
		return instance;
	}

	public static void exportData() throws ExecutionException, InterruptedException {
		if (!instance.isDone())
			instance.get();
		FileWriter.exportDataToCSV(Arrays.asList(instance.anonimous_data), FileWriter.createFile(".csv"));
	}

	public DataEntry[] getAnonimous_data() {
		return anonimous_data;
	}

	public List<DataEntry> getRaw_data() {
		return raw_data;
	}

	private void updateStatus(int index) {
		if (!System.getProperty("useGUI").equals("false"))
			Platform.runLater(() -> updateProgress(index, raw_data.size()));
		System.out.printf("\t- Entry " + (index + 1) + " van de " + raw_data.size() + " aan het verwerken. Dit is %.2f%% van het totaal.\r", (double) (index + 1) / raw_data.size() * 100);
		System.out.flush();

	}

	@Override
	protected List<Task<DataEntry>> call() throws Exception {
		List<Task<DataEntry>> results = new ArrayList<>();
		for (DataEntry raw_entry : this.raw_data) {
			Task<DataEntry> c = new Task<DataEntry>() {
				@Override
				protected DataEntry call() throws Exception {
					DataEntry anonimous_entry = anonimizeEntry(raw_entry);
					if (!System.getProperty("useGUI").equals("false"))
						pushEntryToView(raw_entry, anonimous_entry);
					return anonimous_entry;
				}
			};
			results.add(c);
		}
		//Run all tasks
		System.out.println("Anonimization bezig ...");
		results.forEach(threadpool::execute);
		//Wait for everything to finish.
		while (!threadpool.isShutdown() || !threadpool.isTerminated()) {
			threadpool.shutdown();
		}
		updateStatus(raw_data.size());
		System.out.println("Anonimisatie is klaar!");
		return results;
	}

	private DataEntry anonimizeEntry(DataEntry raw_entry) {
		int index = raw_data.indexOf(raw_entry);
		determineTechnique().doPreProcessing(raw_entry, settings);
		DataEntry anonimous_entry = determineTechnique().anonymize(raw_entry, settings);
		anonimous_data[index] = anonimous_entry;

		updateStatus(index);
		return anonimous_entry;
	}

	private void pushEntryToView(DataEntry raw_entry, DataEntry anonimous_entry) {
		Platform.runLater(() -> {
			dataviewController.addData(raw_entry, anonimous_entry);
		});
	}
	/*
	public void setData(DataEntry... data) {

		DoubleProperty progress = new SimpleDoubleProperty(1);
		pb.progressProperty().bind(progress);
		progress.bind( new DoubleBinding() {
			{
				for (Task<DataEntry> task : results) {
					bind(task.progressProperty());
				}
			}

			@Override
			public double computeValue() {
				return results.stream().collect(Collectors.summingDouble(
						task -> Math.max(task.getProgress(), 0)
				)) / results.size();
			}
		});
		results.forEach(threadpool::execute);

//		// Retrieve individual results and update progress bar.
//		int completedTasks = 0;
//		for (Future<DataEntry> fr : results) {
//			try {
//				fr.get();
//				++completedTasks;
//				updateStatus(completedTasks, results.size(), pb);
//			}
//			 catch (ExecutionException | InterruptedException e) {
//				ErrorHandler.handleException(e);
//			}
//		}

		//Wait for everything to finish.
		while (!threadpool.isShutdown() || !threadpool.isTerminated()) {
			threadpool.shutdown();
		}
		if (dialogStage != null)
			dialogStage.close();
		for (int i = 0; i < raw_data.size(); i++) {
				DataEntry raw_entry = raw_data.get(i);
				DataEntry anonimous_entry = anonimous_data[i];
				if (anonimous_entry == null) {
					try {
						anonimous_data[i] = results.get(i).get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
				if (!System.getProperty("useGUI").equals("false"))
					makeGUI(raw_entry, anonimous_entry);
			}
	}
	 */
}
