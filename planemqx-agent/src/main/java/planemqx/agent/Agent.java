package planemqx.agent;

import static planemqx.common.Log.PRIORITY;

import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import planemqx.broker.ConfigureBroker;
import planemqx.common.Configuration;
import planemqx.common.Log;
import planemqx.common.PayloadJson;

public class Agent {
	public static BrokerPriorityQueue<Object> priorityQueue = new BrokerPriorityQueue<>(Configuration.NB_PRIORITIES,
			Configuration.PRIORITY_QUEUES_CAPACITY);
	private static final String SUB_TOPIC = "+/+/unsorted";
	private final MemoryPersistence persistence;
	private MqttClient client;
	private static final String clientId = "emqx_agent_subscriber";
	public static PayloadJson payloadJson;

	static {
		try {
			ConfigureBroker.configure();
			ObjectMapper mapper = new ObjectMapper();
			payloadJson = mapper.readValue(Paths.get(Configuration.SCENARIO).toFile(), PayloadJson.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Agent() throws MqttException, IOException {
		this.persistence = new MemoryPersistence();
		this.client = new MqttClient(Configuration.BROKER_URL, clientId, persistence);
		System.out.println("Connecting to broker on" + Configuration.BROKER_URL);
	}

	public void start() throws MqttException, IOException {
		if (this.client != null) {
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName(Configuration.AGENT_NAME);
			connOpts.setPassword(Configuration.AGENT_PASSWORD.toCharArray());
			connOpts.setCleanSession(true);
			client.setCallback(new MessageCallback());
			client.connect(connOpts);
			if (Log.ON) {
				PRIORITY.info("{}", () -> "Agent Connected to broker: " + Configuration.BROKER_URL);
			}
			Thread republishThread = new Thread(new RepublishThread());
			republishThread.start();
			client.subscribe(SUB_TOPIC);
		} else
			throw new IOException("null agent client");
	}

	public void disconnectAndClose() throws MqttException {
		this.client.disconnect();
		this.client.close();
	}
}
