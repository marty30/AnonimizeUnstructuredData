package nl.willemsenmedia.utwente.anonymization.dataminingtesters.sentiment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Martijn on 29-3-2016.
 */
public class DoSentimentAnalysis {

	public static String[] data = new String[]{
			"Is wondering why it's always warm when she leaves London in the morning &amp; freezing when she gets to Woking",
			"I would have bet a hundred bucks it would b Cleveland &amp; LA in the finals. Thought it was Lebrons year",
			"06:38 to London Liverpool Street. A bit of time to sort my photographs and to start reading Dot Robot. Didn't find time for a coffee",
			"Maybe eating McDonalds, downing a sherbet fountain &amp; washed it down with warm coke..I feel a bit queasy. And quite a bit foamy"
	};

	public static void main(String[] args) throws JSONException {
		for (String entry : data) {
			System.out.println(doRequest(entry).toString());
		}
	}

	public static JSONObject doRequest(String text) throws JSONException {
		if (text == null || text.equals("")) {
			return new JSONObject().put("error", "Empty text");
		} else if (text.length() > 80000) {
			return new JSONObject().put("error", "Text too long! The limit is 80 000 characters");
		} else {
			URL url = null;
			try {
				url = new URL("http://text-processing.com/api/sentiment/");

				HttpURLConnection uc = (HttpURLConnection) url.openConnection();
				uc.setDoOutput(true);
				uc.setRequestProperty("Accept", "application/json");
				uc.setRequestMethod("POST");
				OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
				wr.write("text=" + text);
				wr.flush();

				StringBuilder sb = new StringBuilder();
				int HttpResult = uc.getResponseCode();
				if (HttpResult == HttpURLConnection.HTTP_OK) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(uc.getInputStream(), "utf-8"));
					String line;
					while ((line = br.readLine()) != null) {
						sb.append(line).append("\n");
					}
					br.close();
					return new JSONObject(sb.toString());
				} else {
					return new JSONObject().put("error", uc.getResponseMessage());
				}
			} catch (IOException e) {
				return new JSONObject().put("error", e.getMessage());
			}
		}
	}
}
