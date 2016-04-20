package nl.willemsenmedia.utwente.anonymization.data;

/**
 * Created by Martijn on 8-3-2016.
 * <p>
 * The possible datatypes for all attributes.
 */
public enum DataType {
	UNSTRUCTURED("Overig"), DATE("Datum"), NAME("Naam"), CLASS("Klasse");

	private final String niceName;

	DataType(String niceName) {
		this.niceName = niceName;
	}

	public String getName() {
		return name();
	}

	@Override
	public String toString() {
		return niceName;
	}
}
