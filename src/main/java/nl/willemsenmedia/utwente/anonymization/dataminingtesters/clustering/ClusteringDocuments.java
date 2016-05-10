/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package nl.willemsenmedia.utwente.anonymization.dataminingtesters.clustering;

import nl.willemsenmedia.utwente.anonymization.data.DataAttribute;
import nl.willemsenmedia.utwente.anonymization.data.DataEntry;
import nl.willemsenmedia.utwente.anonymization.data.DataType;
import nl.willemsenmedia.utwente.anonymization.data.DefaultEntry;
import nl.willemsenmedia.utwente.anonymization.data.reading.FileReader;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.core.*;
import org.carrot2.output.metrics.PrecisionRecallMetric;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.*;

/**
 * This example shows how to cluster a set of documents available as an {@link ArrayList}.
 * This setting is particularly useful for quick experiments with custom data for which
 * there is no corresponding {@link IDocumentSource} implementation. For production use,
 * it's better to implement a {@link IDocumentSource} for the custom document source, so
 * that e.g., the {@link Controller} can cache its results, if needed.
 */
@SuppressWarnings("Duplicates")
public class ClusteringDocuments {
	/* [[[start:clustering-document-list-intro]]]
		 *
         * <div>
         * <p>
         * The easiest way to get started with Carrot2 is to cluster a collection
         * of {@link org.carrot2.core.Document}s. Each document can consist of:
         * </p>
         *
         * <ul>
         * <li>document content: a query-in-context snippet, document abstract or full text,</li>
         * <li>document title: optional, some clustering algorithms give more weight to document titles,</li>
         * <li>document URL: optional, used by the {@link org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm},
         * ignored by other algorithms.</li>
         * </ul>
         *
         * <p>
         * To make the example short, the code shown below clusters only 5 documents. Use
         * at least 20 to get reasonable clusters. If you have access to the query that generated
         * the documents being clustered, you should also provide it to Carrot2 to get better clusters.
         * </p>
         * </div>
         *
         * [[[end:clustering-document-list-intro]]]
         */
	public static void main(String[] args) throws JAXBException {
		/* Prepare Carrot2 documents */
		final ArrayList<Document> documents_raw = new ArrayList<>();
		final ArrayList<Document> documents_anon = new ArrayList<>();

		ArrayList<DataAttribute> headers = new ArrayList<>();
		headers.add(new DataAttribute(DataType.UNSTRUCTURED, ""));
		headers.add(new DataAttribute(DataType.CLASS, ""));
		Settings settings = Settings.getDefault();
		settings.getSettingsMap().put("bevat_kopteksten", new Settings.Setting("bevat_kopteksten", "true"));
//		settings.getSettingsMap().get("beginrij").setValue("" + Integer.parseInt(settings.getSettingsMap().get("beginrij").getValue()) + 1);
		List<DataEntry> dataEntries_raw = FileReader.readFile(new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus.csv"), settings, headers);
		List<DataEntry> dataEntries_anon = FileReader.readFile(new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus_GeneralizeOrSuppress.csv"), settings, headers);
		for (DataEntry dataEntry : dataEntries_raw) {
			StringBuilder content = new StringBuilder();
			LinkedList<String> partition = new LinkedList<>();
			for (DataAttribute atrr : dataEntry.getDataAttributes()) {
				if (!atrr.getDataType().equals(DataType.CLASS))
					content.append(atrr.getData());
				else
					partition.add(atrr.getData());
			}
			Document doc = new Document(dataEntry.getHeaders().get(0).getData(), content.toString());
			doc.setField(Document.PARTITIONS, partition);
			documents_raw.add(doc);
		}
		for (DataEntry dataEntry : dataEntries_anon) {
			StringBuilder content = new StringBuilder();
			LinkedList<String> partition = new LinkedList<>();
			for (DataAttribute atrr : dataEntry.getDataAttributes()) {
				if (!atrr.getDataType().equals(DataType.CLASS))
					content.append(atrr.getData());
				else
					partition.add(atrr.getData());
			}
			Document doc = new Document(dataEntry.getHeaders().get(0).getData(), content.toString());
			doc.setField(Document.PARTITIONS, partition);
			documents_anon.add(doc);
		}

		/* A controller to manage the processing pipeline. */
		final Controller controller = ControllerFactory.createSimple();

		/* Perform clustering by topic using the Lingo algorithm.*/
		final ProcessingResult RawByTopicClusters = controller.process(documents_raw, null, LingoClusteringAlgorithm.class);
		final List<Cluster> RawClustersByTopic = RawByTopicClusters.getClusters();
		final PrecisionRecallMetric RawMetric = new PrecisionRecallMetric();
		RawMetric.clusters = RawClustersByTopic;
		RawMetric.documents = documents_raw;
		RawMetric.calculate();
		//Print results from raw clustering
		System.out.println("Clustering result of raw documents");
		System.out.println("There are " + RawMetric.clusters.size() + " clusters from " + RawMetric.documents.size() + " documents. \r\n" +
				"Precision: " + RawMetric.weightedAveragePrecision + "\r\n" +
				"Recall: " + RawMetric.weightedAverageRecall + "\r\n" +
				"F-measure: " + RawMetric.weightedAverageFMeasure + "\r\n");

		final ProcessingResult AnonByTopicClusters = controller.process(documents_anon, null, LingoClusteringAlgorithm.class);
		final List<Cluster> AnonClustersByTopic = AnonByTopicClusters.getClusters();
		final PrecisionRecallMetric AnonMetric = new PrecisionRecallMetric();
		AnonMetric.clusters = AnonClustersByTopic;
		AnonMetric.documents = documents_anon;
		AnonMetric.calculate();
		//Print results from anonymous clustering
		System.out.println("Clustering result of anonymous documents");
		System.out.println("There are " + AnonMetric.clusters.size() + " clusters from " + AnonMetric.documents.size() + " documents. \r\n" +
				"Precision: " + AnonMetric.weightedAveragePrecision + "\r\n" +
				"Recall: " + AnonMetric.weightedAverageRecall + "\r\n" +
				"F-measure: " + AnonMetric.weightedAverageFMeasure + "\r\n");


		//Now compare the two sets of clusters
		List<Double> percentages_best_possible = new LinkedList<>();
		List<Double> percentages_one_to_one = new LinkedList<>();
		Map<Cluster, Map<Cluster, Integer>> possible_matches = new HashMap<>(RawClustersByTopic.size());
		for (Cluster raw_cluster : RawClustersByTopic) {
			//We have a raw cluster, now determine the best anonymous cluster
			Map<Cluster, Integer> matches_for_raw_cluster = new HashMap<>(AnonClustersByTopic.size());
			for (Cluster anon_cluster : AnonClustersByTopic) {//.get(RawClustersByTopic.indexOf(raw_cluster));
				int the_same = 0;
				for (Document raw_doc : raw_cluster.getAllDocuments()) {
					if (anon_cluster.getAllDocuments().stream().anyMatch(anon_doc -> anon_doc.getStringId().equals(raw_doc.getStringId()))) {
						the_same++;
					}
				}
				if (Objects.equals(raw_cluster.getId(), anon_cluster.getId()))
					percentages_one_to_one.add((double) the_same / raw_cluster.size());
				matches_for_raw_cluster.put(anon_cluster, the_same);
			}
			possible_matches.put(raw_cluster, sortByValue(matches_for_raw_cluster));
			Map.Entry<Cluster, Integer> best_match = matches_for_raw_cluster.entrySet().stream().max((o1, o2) -> o1.getValue().compareTo(o2.getValue())).orElse(new DefaultEntry<>(null, 0));
			percentages_best_possible.add((double) best_match.getValue() / raw_cluster.size());
		}

		// Find the optimal match between the two lists
		// TODO: 19-4-2016 Checken of dit inderdaad optimaal is
		Map<Cluster, Map.Entry<Cluster, Integer>> real_matches = new LinkedHashMap<>(RawClustersByTopic.size());
		//Now find the best match. To do this in an optimal way, we first need to find the two clusters with the most similarities.
		for (Map.Entry<Cluster, Map<Cluster, Integer>> entry : possible_matches.entrySet()) {
			Cluster raw_cluster = entry.getKey();
			for (Map.Entry<Cluster, Integer> current_match : entry.getValue().entrySet()) {
				if (real_matches.entrySet().stream().noneMatch(clusterMapEntry -> clusterMapEntry.getValue().getKey().equals(current_match.getKey()))) {
					real_matches.put(raw_cluster, current_match);
					break;
				}
			}
		}

//		ConsoleFormatter.displayClusters(RawClustersByTopic);
//		ConsoleFormatter.displayClusters(AnonClustersByTopic);
		System.out.println("Equality (one-to-one): " + percentages_one_to_one.stream().mapToDouble(a -> a).average().orElse(0));
		System.out.println("Equality (best possible): " + percentages_best_possible.stream().mapToDouble(a -> a).average().orElse(0));
		System.out.println("Equality (optimal match): " + real_matches.entrySet().stream().mapToDouble((entry) -> (double) entry.getValue().getValue() / entry.getKey().size()).average().orElse(0));
		//print de matches
		for (Map.Entry<Cluster, Map.Entry<Cluster, Integer>> match : real_matches.entrySet()) {
			System.out.println(match.getKey().getId() + " -> " + match.getValue().getKey().getId() + " (" + match.getValue().getValue() + ")");
		}
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
		Map<K, V> result = new LinkedHashMap<>();
		map.entrySet().stream().sorted((c1, c2) -> c2.getValue().compareTo(c1.getValue())).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
		return result;
	}

	//			File folder = new File("path to dir");
//			String temp;
//			try {
//				for (final File fileEntry : folder.listFiles()) {
//					if (fileEntry.isDirectory()) {
//						// Do nothing with directories!
//						continue;
//					} else {
//						if (fileEntry.isFile()) {
//							temp = fileEntry.getName();
//							if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("txt")) {
//
//			        	  /* Lees het specifieke bestand in */
//								Scanner sc = new Scanner(fileEntry.toPath());
//								String text = "";
//								while (sc.hasNext()) text += sc.nextLine() + "\n";
//								sc.close();
//								documents.add(new Document(temp, text));
//							}
//						}
//					}
//				}
//			} catch (IOException e) {
//
//			}
//								for (String[] row : new File(Protocol.LOC_FIRST).listFiles()) {
//				documents.add(new Document(row[1], row[2], row[0]));
//			}

}