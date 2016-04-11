package nl.willemsenmedia.utwente.anonymization.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.handling.*;
import nl.willemsenmedia.utwente.anonymization.data.writing.FileWriter;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

import java.net.URL;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by Martijn on 20-2-2016.
 * <p>
 * The controller for the dataview part of the GUI
 */
public class DataviewController implements Initializable {
	private static final String AttributeSeparator = "--------------------------";
	private List<DataEntry> raw_data;
	private DataEntry[] anonimous_data;
	@FXML
	private TabPane tabPane;
	private Settings settings;
	private int tabnr;
	private int max_tabs;
	private LinkedList<Thread> threatList;

	public void setData(DataEntry... data) {
		this.raw_data = Arrays.asList(data);
		this.anonimous_data = new DataEntry[raw_data.size()];
		if (!System.getProperty("useGUI").equals("false"))
			tabPane.getTabs().clear();
		tabnr = 0;
		max_tabs = 25;
		ExecutorService threadpool = Executors.newFixedThreadPool(5);
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
		List<Callable<Object>> todo = new ArrayList<>(raw_data.size());
		todo.addAll(this.raw_data.stream().map(raw_entry -> Executors.callable((PrivilegedAction<DataEntry>) () -> {
			int index = raw_data.indexOf(raw_entry);
			updateStatus(index, raw_data.size());
			determineTechnique().doPreProcessing(raw_entry, settings);
			DataEntry anonimous_entry = determineTechnique().anonymize(raw_entry, settings);
			anonimous_data[index] = anonimous_entry;
			return anonimous_entry;
		})).collect(Collectors.toList()));

		//Start all
		List<Future<Object>> answers = null;
		try {
			answers = threadpool.invokeAll(todo);
			System.out.println("Done!");
		} catch (InterruptedException e) {
			ErrorHandler.handleException(e);
		}

		//Wait for everything to finish.
		while (!threadpool.isShutdown() || !threadpool.isTerminated()) {
			threadpool.shutdown();
		}

			for (int i = 0; i < raw_data.size(); i++) {
				DataEntry raw_entry = raw_data.get(i);
				DataEntry anonimous_entry = anonimous_data[i];
				if (anonimous_entry == null) {
					if (answers == null) {
						throw new NullPointerException("answers is null");
					} else {//if (answers.get(i).isDone()){
						try {
							anonimous_data[i] = ((DataEntry) answers.get(i).get());
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
					}
				}
				if (!System.getProperty("useGUI").equals("false"))
					makeGUI(raw_entry, anonimous_entry);
			}
	}

	private void updateStatus(int index, int total) {
		if (!System.getProperty("useGUI").equals("false")) {
			// TODO: 8-4-2016 make progress bar see: https://trello.com/c/QLNoolrc/31-voeg-een-progress-bar-toe-tijdens-het-anonimiseerproces
		} else {
			if (index == 0)
				System.out.println("Anonimization bezig ...");
			System.out.printf("\t- Entry " + (index + 1) + " van de " + total + " aan het verwerken. Dit is %.2f%% van het totaal.\r", (double) (index + 1) / total * 100);
			System.out.flush();
		}
	}

	public void makeGUI(DataEntry raw_entry, DataEntry anonimous_entry) {
		if (tabnr < max_tabs) {
			Tab tab = new Tab();
			//Create title
			String title = anonimous_entry.getDataAttributes().get(0).getData();
			if (title.length() > 15)
				title = title.substring(0, 15) + "...";
			tab.setText("Data: " + title);

			HBox hbox = new HBox();
			String content = "";
			for (DataAttribute attribute : anonimous_entry.getDataAttributes()) {
				content += attribute.getData() + "\n" + AttributeSeparator + "\n";
			}
			hbox.getChildren().add(new Label(content.trim()));
			hbox.setAlignment(Pos.TOP_LEFT);
			ScrollPane sp = new ScrollPane();
			sp.setContent(hbox);
			tab.setContent(sp);
			tabPane.getTabs().add(tab);
			tabnr++;
		}
	}

	private AnonymizationTechnique determineTechnique() {
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void exportData(ActionEvent event) {
		FileWriter.exportDataToCSV(Arrays.asList(anonimous_data), FileWriter.createFile(".csv"));

	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
