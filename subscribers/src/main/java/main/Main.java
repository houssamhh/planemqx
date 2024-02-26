package main;

import static common.Log.GEN;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.opencsv.CSVWriter;

import clients.Application;
import common.Configuration;
import common.JSONDataParser;
import common.Log;

public class Main {

	static final int EXPERIMENTATION_DURATION = Configuration.EXPERIMENT_DURATION;

	public static void main(String[] args) throws IOException, InterruptedException {
		String jsonData = new String(Files.readAllBytes(Paths.get(Configuration.SCENARIO)));
		ConcurrentHashMap<String, Application> applications = JSONDataParser.getApplications(jsonData);
		if (Log.ON) {
			GEN.info("{}", () -> "Subscribing applications");
		}
		applications.entrySet().forEach(app -> {
			app.getValue().connect(Configuration.BROKER_URL);
			app.getValue().subscribe();
		});
		System.out.println("**************************** subscribers START OF EXPERIMENT********************************");
		Thread.sleep(EXPERIMENTATION_DURATION);
		
		//Writing summary of results to result.csv
		String[] header = { "app, topic, category, priority, msgreceived, responsetime" };
		CSVWriter writer = new CSVWriter(new FileWriter("results/result.csv"));
		writer.writeNext(header);
		applications.entrySet().forEach(app -> {
			String appId = app.getValue().getClientId();
			String category = app.getValue().getCategory().name();
			String priority = String.valueOf(app.getValue().getPriority());
			String nbReceivedMsgs = String.valueOf(app.getValue().getNbMessagesReceived());
			try {
				HashMap<String, Double> latencyPerTopic = app.getValue().getLatencyPerTopic();
				for (String t : latencyPerTopic.keySet()) {
					if (Log.ON) {
						GEN.info("{}", () -> "Response time for: " + app.getValue().getClientId() + t + " = "
								+ latencyPerTopic.get(t));
					}
					String[] data = { appId, t, category, priority, nbReceivedMsgs,
							String.valueOf(latencyPerTopic.get(t)) };
					writer.writeNext(data);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		writer.close();
		
		
		System.out.println("*************************** subscribers END OF EXPERIMENT********************************");
//        applications.entrySet().forEach(app -> app.getValue().unsubscribe());
//        applications.entrySet().forEach(app -> app.getValue().disconnect());

		Thread.sleep(1000);
		System.exit(0);

	}

}
