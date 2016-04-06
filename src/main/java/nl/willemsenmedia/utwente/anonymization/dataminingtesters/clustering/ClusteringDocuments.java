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
import nl.willemsenmedia.utwente.anonymization.data.reading.FileReader;
import nl.willemsenmedia.utwente.anonymization.settings.Settings;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.synthetic.ByUrlClusteringAlgorithm;
import org.carrot2.core.*;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This example shows how to cluster a set of documents available as an {@link ArrayList}.
 * This setting is particularly useful for quick experiments with custom data for which
 * there is no corresponding {@link IDocumentSource} implementation. For production use,
 * it's better to implement a {@link IDocumentSource} for the custom document source, so
 * that e.g., the {@link Controller} can cache its results, if needed.
 */
public class ClusteringDocuments {
	public static void main(String[] args) throws JAXBException {
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
		{
			// [[[start:clustering-document-list]]]
            /* A few example documents, normally you would need at least 20 for reasonable clusters. */
			final String[][] data = new String[][]
					{
							{
									"http://en.wikipedia.org/wiki/Data_mining",
									"Data mining - Wikipedia, the free encyclopedia",
									"Article about knowledge-discovery in databases (KDD), the practice of automatically searching large stores of data for patterns."
							},

							{
									"http://www.ccsu.edu/datamining/resources.html",
									"CCSU - Data Mining",
									"A collection of Data Mining links edited by the Central Connecticut State University ... Graduate Certificate Program. Data Mining Resources. Resources. Groups ..."
							},

							{
									"http://www.kdnuggets.com/",
									"KDnuggets: Data Mining, Web Mining, and Knowledge Discovery",
									"Newsletter on the data mining and knowledge industries, offering information on data mining, knowledge discovery, text mining, and web mining software, courses, jobs, publications, and meetings."
							},

							{
									"http://en.wikipedia.org/wiki/Data-mining",
									"Data mining - Wikipedia, the free encyclopedia",
									"Data mining is considered a subfield within the Computer Science field of knowledge discovery. ... claim to perform \"data mining\" by automating the creation ..."
							},

							{
									"http://www.anderson.ucla.edu/faculty/jason.frand/teacher/technologies/palace/datamining.htm",
									"Data Mining: What is Data Mining?",
									"Outlines what knowledge discovery, the process of analyzing data from different perspectives and summarizing it into useful information, can do and how it works."
							},
					};

            /* Prepare Carrot2 documents */
			final ArrayList<Document> documents = new ArrayList<Document>();
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



//			List<DataEntry> dataEntries = FileReader.readFile(new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\SFU_Review_Corpus.csv"), Settings.getDefault(), null);
			List<DataEntry> dataEntries = FileReader.readFile(new File("C:\\Users\\Martijn\\Dropbox\\Studie\\College\\Module11&12\\ResearchProject-Ynformed\\JavaApplicatie\\AnonimizeUnstructuredData\\export_2016-04-04_163842.684.csv"), Settings.getDefault(), null);
			for (DataEntry dataEntry: dataEntries){
				StringBuilder content = new StringBuilder();
				for (DataAttribute atrr : dataEntry.getDataAttributes()) {
					content.append(atrr.getData());
				}
				documents.add(new Document(dataEntry.getHeaders().get(0).getData(), content.toString()));
			}

            /* A controller to manage the processing pipeline. */
			final Controller controller = ControllerFactory.createSimple();

            /*
             * Perform clustering by topic using the Lingo algorithm. Lingo can
             * take advantage of the original query, so we provide it along with the documents.
             */
			final ProcessingResult byTopicClusters = controller.process(documents, "data mining",
					LingoClusteringAlgorithm.class);
			final List<Cluster> clustersByTopic = byTopicClusters.getClusters();

            /* Perform clustering by domain. In this case query is not useful, hence it is null. */
			final ProcessingResult byDomainClusters = controller.process(documents, null,
					ByUrlClusteringAlgorithm.class);
			final List<Cluster> clustersByDomain = byDomainClusters.getClusters();
			// [[[end:clustering-document-list]]]

			ConsoleFormatter.displayClusters(clustersByTopic);
			System.out.println("---------------------------------");
			ConsoleFormatter.displayClusters(clustersByDomain);
			System.out.println("---------------------------------");
			ConsoleFormatter.displayResults(controller.process(documents, "data mining",
					LingoClusteringAlgorithm.class, ByUrlClusteringAlgorithm.class));

		}
	}
}