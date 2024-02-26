package planemqx.broker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import planemqx.clients.Application;
import planemqx.clients.ApplicationCategory;
import planemqx.common.AIDataParser;
import planemqx.common.Configuration;
import planemqx.common.JSONDataParser;
import planemqx.common.Pair;

public class ConfigureBroker {

	private ConfigureBroker() {
	}

	/*
	 * fork : the topic of republishing in rules are in this form:
	 * topic9/app_app3/unsorted
	 */
	public static Broker configure() throws IOException {
		// Initialize broker
		Broker broker = new Broker(Configuration.BROKER_URL, Configuration.BROKER_API_URL, Configuration.LOGIN,
				Configuration.PASSWORD, Configuration.SERVER_URL);
		// clear previous configuration
		broker.cleanRules();
		// Get topics from JSON file
		String jsonData = new String(Files.readAllBytes(Paths.get(Configuration.SCENARIO)));
		Map<String, List<String>> topicsSubscribers = JSONDataParser.getTopicsDetails(jsonData);
		HashMap<String, Application> applications = JSONDataParser.getApplications(jsonData);
		// Get broker configuration from AIPlanner output file and the JSON file
		Pair<Map<ApplicationCategory, Double>, List<ApplicationCategory>> conf = AIDataParser
				.readData(Configuration.CONFIGURATION);
		Map<ApplicationCategory, Double> categoryAppDropping = conf.getFirst();
		Integer brokerCapacity = new JSONObject(jsonData).getInt("brokerCapacity");
		// Configure broker
		// Capacity
		broker.configureCapacity(brokerCapacity >= 0 ? brokerCapacity : 50000);
		// TODO: add to the topic array (sampleScenario.json file)
		// Priorities
		/*
		 * Map<String, Integer> topics_priorities = new HashMap<String, Integer>();
		 * applications.entrySet().forEach(app -> { Integer index =
		 * categoryApp_priorities.indexOf(app.getValue().getCategory()); if (index > -1)
		 * { app.getValue().getTopics().forEach(topic -> topics_priorities.put(topic,
		 * index * (-10) + 50)); } });
		 * broker.configureTopicPriorities(topics_priorities);
		 */
		// Dropping and fork
		topicsSubscribers.entrySet().forEach(e -> {
			// init
			Map<ApplicationCategory, List<String>> data = new HashMap<>();
			categoryAppDropping.entrySet().forEach(key -> data.put(key.getKey(), new ArrayList<String>()));

			// get rewritten topics for each category
			List<String> clientsNoDropping = new ArrayList<>();
			e.getValue().forEach(appId -> {
				List<String> list = data.get(applications.get(appId).getCategory());
				if (list != null) {
					list.add(appId);
				} else {
					clientsNoDropping.add(appId);
				}
			});

			// create rules
			// if no dropping => fork
			if (!clientsNoDropping.isEmpty()) {
				broker.fork(e.getKey(), clientsNoDropping);
			}
			// else => dropping + fork
			categoryAppDropping.entrySet().forEach(drop -> {
				List<String> clientsDropping = data.get(drop.getKey());
				if (clientsDropping != null && !clientsDropping.isEmpty()) {
					Double droppingRate = drop.getValue();
					broker.forkWithDropping(e.getKey(), clientsDropping, droppingRate * 0.01);
				}
				data.put(drop.getKey(), null);
			});
		});

		return broker;
	}

	public static Broker configureWithoutDropper() throws IOException {
		// Initialize broker
		Broker broker = new Broker(Configuration.BROKER_URL, Configuration.BROKER_API_URL, Configuration.LOGIN,
				Configuration.PASSWORD, Configuration.SERVER_URL);
		// clear previous configuration
		broker.cleanRules();
		// Get topics from JSON file
		String jsonData = new String(Files.readAllBytes(Paths.get(Configuration.SCENARIO)));
		Map<String, List<String>> topics_subscribers = JSONDataParser.getTopicsDetails(jsonData);
		HashMap<String, Application> applications = JSONDataParser.getApplications(jsonData);
		// Get broker configuration from AIPlanner output file and the JSON file
		Pair<Map<ApplicationCategory, Double>, List<ApplicationCategory>> conf = AIDataParser
				.readData(Configuration.CONFIGURATION);
		Map<ApplicationCategory, Double> categoryApp_dropping = conf.getFirst();
		Integer brokerCapacity = new JSONObject(jsonData).getInt("brokerCapacity");
		// Configure broker
		// Capacity
		broker.configureCapacity(brokerCapacity >= 0 ? brokerCapacity : 65535);
		// TODO: add to the topic array (sampleScenario.json file)
		// Priorities
		/*
		 * Map<String, Integer> topics_priorities = new HashMap<String, Integer>();
		 * applications.entrySet().forEach(app -> { Integer index =
		 * categoryApp_priorities.indexOf(app.getValue().getCategory()); if (index > -1)
		 * { app.getValue().getTopics().forEach(topic -> topics_priorities.put(topic,
		 * index * (-10) + 50)); } });
		 * broker.configureTopicPriorities(topics_priorities);
		 */
		// Dropping and fork
		topics_subscribers.entrySet().forEach(e -> {
			// init
			Map<ApplicationCategory, List<String>> data = new HashMap<>();
			categoryApp_dropping.entrySet().forEach(key -> data.put(key.getKey(), new ArrayList<>()));
			// get rewritten topics for each category
			List<String> clientsNoDropping = new ArrayList<>();
			e.getValue().forEach(appId -> {
				List<String> list = data.get(applications.get(appId).getCategory());
				if (list != null) {
					list.add(appId);
				} else {
					clientsNoDropping.add(appId);
				}
			});
			// create rules
			// if no dropping => fork
			if (!clientsNoDropping.isEmpty()) {
				broker.forkWithoutDropping(e.getKey(), clientsNoDropping);
			}
			// else => dropping + fork
			categoryApp_dropping.entrySet().forEach(drop -> {
				List<String> clientsDropping = data.get(drop.getKey());
				if (clientsDropping != null && !clientsDropping.isEmpty()) {
					Double droppingRate = drop.getValue();
					broker.forkAllWithDropping(e.getKey(), clientsDropping, droppingRate * 0.01);
				}
				data.put(drop.getKey(), null);
			});
		});
		return broker;
	}
}
