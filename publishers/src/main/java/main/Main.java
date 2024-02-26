package main;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.opencsv.CSVWriter;

import clients.Device;
import common.Configuration;
import common.JSONDataParser;
import common.PublishMessage;
import commonPriorities.Statistics;

public class Main {
	static final int EXPERIMENT_DURATION = Configuration.EXPERIMENT_DURATION;

	public static void main(String[] args) throws IOException, InterruptedException, MqttException {
		String jsonData = new String(Files.readAllBytes(Paths.get(Configuration.SCENARIO)));
		HashMap<String, Device> devices = JSONDataParser.getDevices(jsonData);
		CSVWriter writer = new CSVWriter(new FileWriter("results/result.csv"));
		String[] header = { "device", "topic", "nbMessages" };
		writer.writeNext(header);
		writer.close();
		List<Thread> threads = new ArrayList<>();
		devices.entrySet().forEach(dev -> {
			try {
				dev.getValue().connect(Configuration.BROKER_URL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		System.out.println("**************************** publishers START OF EXPERIMENT********************************");
		for (Map.Entry<String, Device> d : devices.entrySet()) {
			Thread thread = new Thread(new PublishMessage(d.getValue(), EXPERIMENT_DURATION));
			threads.add(thread);
			thread.start();
		}
		threads.forEach(thread -> {
			try {
				thread.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		devices.entrySet().forEach(dev -> dev.getValue().disconnect());
		Statistics.addSeperation();
		Thread.sleep(1000); 
		System.out.println(Main.class.getCanonicalName()
				+ " ***************************publishers END OF EXPERIMENT********************************");
		System.exit(0);
	}
}
