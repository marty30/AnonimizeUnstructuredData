package nl.willemsenmedia.utwente.anonymization.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martijn on 20-2-2016.
 * <p>
 * A data entry is a set of data attributes.
 */
public class DataEntry implements Cloneable {

	public boolean isPreProcessed = false;
	public boolean isAnonymized = false;
	private List<DataAttribute> headers;
	private List<DataAttribute> dataAttributes;

	public DataEntry(List<DataAttribute> headers, DataAttribute... dataAttributes) {
		this.dataAttributes = new LinkedList<>();
		this.headers = headers;
		Collections.addAll(this.dataAttributes, dataAttributes);
	}

	public void addDataAttribute(DataAttribute... dataAttributes) {
		if (this.dataAttributes == null)
			this.dataAttributes = new LinkedList<>();
		Collections.addAll(this.dataAttributes, dataAttributes);
	}


	public List<DataAttribute> getDataAttributes() {
		return dataAttributes;
	}

	public List<DataAttribute> getHeaders() {
		return headers;
	}

	@Override
	public String toString() {
		String listString = super.toString() + "\n";

		for (DataAttribute s : dataAttributes) {
			listString += "\t" + s.toString() + "\n";
		}
		return listString;
	}

	public void update(DataEntry newEntry) {
		this.dataAttributes = newEntry.getDataAttributes();
	}

	@Override
	public DataEntry clone() {
		DataEntry new_entry = new DataEntry(headers);
		getDataAttributes().forEach(dataAttribute -> new_entry.addDataAttribute(dataAttribute.clone()));
		return new_entry;
	}
}
