package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

/**
 * Created by Martijn on 19-2-2016.
 * <p>
 * The abstract class that all anonymization techniques should extend.
 */
public abstract class AnonymizationTechnique {

	public abstract DataEntry anonymize(DataEntry dataEntry, Settings settings);

	public DataEntry doPreProcessing(DataEntry dataEntry, Settings settings) {
		return dataEntry;
	}
}
