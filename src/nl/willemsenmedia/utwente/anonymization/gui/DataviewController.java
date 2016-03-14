package nl.willemsenmedia.utwente.anonymization.gui;

import nl.willemsenmedia.utwente.anonymization.data.DataEntry;

import java.util.List;

/**
 * Created by Martijn on 20-2-2016.
 * <p>
 * The controller for the dataview part of the GUI
 */
public class DataviewController {
	private List<DataEntry> data;

	public void setData(List<DataEntry> data) {
		this.data = data;
	}
}
