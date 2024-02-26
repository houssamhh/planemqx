package clients;

import static common.Log.GEN;
import static common.Log.STAT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;

import common.Configuration;
import common.Log;
import common.MessageResult;

/**
 * this class contains the logic of a subscriber broker client.
 *
 */
public class Application extends BrokerClient implements MqttCallback {
	/**
	 * topics priority to which the application subscribes.
	 */
	private Integer priority;

	/**
	 * category application.
	 */
	private ApplicationCategory category;
	/**
	 * the sum of all the latencies of all messages received
	 */
	private double delays = 0;
	/**
	 * nb Messages received
	 */
	private int nbMessagesReceived = 0;
	private ObjectMapper objectMapper = new ObjectMapper();
	private double averageDelayForApp = 0;
	private Map<String, Double> responseTimePerTopic;
	private Map<String, Integer> nbMsgReceivedPerTopic;
	private Map<String, Double> sumOfLatenciesPerTopic;
	private ArrayList<MessageResult> messageResultList;

	/**
	 * constructor.
	 * 
	 * @param applicationId
	 * @param applicationName
	 * @param priority
	 * @param category
	 * @param topics
	 */
	public Application(final String applicationId, final String applicationName, final Integer priority,
			final ApplicationCategory category, final List<String> topics) {
		super(applicationId, applicationName, topics);
		responseTimePerTopic = new HashMap<>();
		nbMsgReceivedPerTopic = new HashMap<>();
		sumOfLatenciesPerTopic = new HashMap<>();
		messageResultList = new ArrayList<MessageResult>();
		if (priority < 0 || priority > 255) {
			throw new IllegalArgumentException();
		}
		this.priority = priority;
		this.category = category;
		if (Log.ON) {
			GEN.info("{}", () -> this.toString());
		}
		/// Statistics stat=new Statistics();
	}

	public int getNbMessagesReceived() {
		return nbMessagesReceived;
	}

	public double getDelays() {
		return delays;
	}

	/**
	 * subscribe the application to topics.
	 */
	public void subscribe() {
		topics.stream().forEach(topic -> {
			Long time = System.currentTimeMillis();
			try {
				mqttClient.subscribe(topic + "/" + clientId);
				this.responseTimePerTopic.put(topic + "/" + clientId, (double) 0);
				this.nbMsgReceivedPerTopic.put(topic + "/" + clientId, 0);
				this.sumOfLatenciesPerTopic.put(topic + "/" + clientId, 0d);
				if (Log.ON) {
					GEN.info("{}", () -> clientId + " subscribes to topic " + topic);
				}
			} catch (MqttException e) {
			}
			if (Log.ON) {
				STAT.info("{}", () -> "Subscribe [" + clientId + ", " + topic + ", "
						+ (System.currentTimeMillis() - time) + "]");
			}
		});
	}

	/**
	 * unsubscribe the application to topics.
	 */
	public void unsubscribe() {
		MqttMessage message = new MqttMessage("unsubscribe".getBytes());
		topics.stream().forEach(topic -> {
			responseTimePerTopic = new HashMap<>();
			nbMsgReceivedPerTopic = new HashMap<>();
			Long time = System.currentTimeMillis();
			if (Log.ON) {
				STAT.info("{}", () -> "Unsubscribe [" + clientId + ", " + topic + ", " + time.toString() + "]");
			}
			try {
				mqttClient.publish(topic + "/" + clientId, message);
				if (Log.ON) {
					GEN.info("{}", () -> clientId + " unsubscribes from " + topic);
				}
			} catch (MqttException e) {
				if (Log.ON) {
					GEN.error("{}", () -> clientId + " could not unsubscribe from " + topic);
				}
				e.printStackTrace();
			}
		});
	}

	/**
	 * @param brokerUrl
	 */
	@Override
	public void connect(final String brokerUrl) {

		try {
			this.mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName(Configuration.AGENT_NAME);
			connOpts.setPassword(Configuration.AGENT_PASSWORD.toCharArray());
			connOpts.setMaxInflight(65535);
			connOpts.setCleanSession(true);
			mqttClient.setCallback(this);
			mqttClient.connect(connOpts);

			if (Log.ON) {
				GEN.info("{}", () -> clientId + ": connected");
			}
		} catch (MqttException e) {
			if (Log.ON) {
				GEN.error("{}", () -> clientId + ": could not connect");
			}
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Application " + super.toString() + "[priority=" + priority + ", category=" + category + "]";
	}

	public Integer getPriority() {
		return priority;
	}

	public ApplicationCategory getCategory() {
		return category;
	}

	@Override
	public void connectionLost(Throwable cause) {

	}

	//Write results to csv file at runtime
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		Map<String, Object> messageAsObject = objectMapper.readValue(new String(message.getPayload()),
				new TypeReference<>() {
				});
		long timestamp = (long) messageAsObject.get("timestamp");
		long latency = (System.currentTimeMillis() - timestamp);
		if (Log.ON) {
			STAT.info("{}",
					() -> "Message arrived to topic " + topic + " with response time " + Double.toString(latency));
		}
		File file = new File("results/responsetimes.csv");
		FileWriter outputfile = new FileWriter(file, true);
		CSVWriter writer = new CSVWriter(outputfile);
		String[] data = { Long.toString(timestamp), topic, Long.toString(latency) };
		writer.writeNext(data);
		writer.close();
		this.sumOfLatenciesPerTopic.put(topic, sumOfLatenciesPerTopic.get(topic) + latency);
		this.nbMessagesReceived++;
		this.nbMsgReceivedPerTopic.put(topic, this.nbMsgReceivedPerTopic.get(topic) + 1);
		if (topic.equals("/unsubscribe")) {
			if (Log.ON) {
				STAT.info("{}", () -> this.clientId + "unsubscribing from " + topic + "/" + this.clientId);
			}
		}
		if (this.nbMsgReceivedPerTopic.get(topic) == 0) {
			if (Log.ON) {
				STAT.info("{}", () -> "topic error " + topic);
			}
		}
	}
	

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {

	}

	public HashMap<String, Double> getLatencyPerTopic() throws IOException {
		HashMap<String, Double> latencyPerTopic = new HashMap<String, Double>();
		for (String topicKey : sumOfLatenciesPerTopic.keySet()) {
			latencyPerTopic.put(topicKey, sumOfLatenciesPerTopic.get(topicKey) / nbMsgReceivedPerTopic.get(topicKey));
		}
		return latencyPerTopic;
	}

	public double calculateAverageDelay() {
//		return this.delays/this.nbMessagesReceived;
		return this.averageDelayForApp;
	}

	public double getAverageResponseTime() {
		return this.averageDelayForApp;
	}

	public double averageResponseTimePerTopic(String topic) {

		return this.responseTimePerTopic.get(topic) / this.nbMsgReceivedPerTopic.get(topic);
	}

	public Map<String, Double> getResponseTimePerTopic() {
		return responseTimePerTopic;
	}

	public Map<String, Integer> getNbMsgReceivedPerTopic() {
		return nbMsgReceivedPerTopic;
	}

	public ArrayList<MessageResult> getResponseTimePerMessage() {
		// TODO Auto-generated method stub
		return messageResultList;
	}
}