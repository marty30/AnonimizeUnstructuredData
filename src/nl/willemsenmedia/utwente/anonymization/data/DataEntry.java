package nl.willemsenmedia.utwente.anonymization.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martijn on 20-2-2016.
 * <p>
 * A data entry is a set of data attributes.
 */
public class DataEntry {

	private List<DataAttribute> dataAttributes;

	public DataEntry(DataAttribute... dataAttributes) {
		this.dataAttributes = new LinkedList<>();
		Collections.addAll(this.dataAttributes, dataAttributes);
	}

	public List<DataAttribute> getDataAttributes() {
		return dataAttributes;
	}

	@Override
	public String toString() {
		String listString = super.toString() + "\n";

		for (DataAttribute s : dataAttributes) {
			listString += "\t" + s.toString() + "\n";
		}
		return listString;
	}
}
