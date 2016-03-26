package nl.willemsenmedia.utwente.anonymization.data.handling;

import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;

/**
 * Created by Martijn on 8-3-2016.
 * <p>
 * This technique focusses on the techniques used in literature to achieve k-anonymity.
 */
public class GeneralizeOrSuppress extends AnonymizationTechnique {
	private final int k;

	public GeneralizeOrSuppress(int k) {
		this.k = k;
	}

	@Override
	public DataEntry anonymize(DataEntry dataEntry, Settings settings) {
		// Get data

		// Sort all words

		// Check the occurrences (enough k?)

		// Try generalization using Cornetto

		// Check again (enough k?)

		// Still not enough? Then suppress

		// Finally return the data
		return null;
	}
}
