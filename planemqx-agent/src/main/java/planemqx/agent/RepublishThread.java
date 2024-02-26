package planemqx.agent;

import static planemqx.common.Log.PRIORITY;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import planemqx.common.Configuration;
import planemqx.common.Log;

public class RepublishThread implements Runnable, MqttCallback {

	private MqttClient client;
	private final String broker;
	private final MemoryPersistence persistence;
	private static final String clientId = "emqx_client_publisher_2";

	public RepublishThread() throws IOException, MqttException {
		this.broker = Configuration.BROKER_URL;
		this.persistence = new MemoryPersistence();
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setUserName(Configuration.AGENT_NAME);
		connOpts.setPassword(Configuration.AGENT_PASSWORD.toCharArray());
		connOpts.setMaxInflight(1000000);
		connOpts.setCleanSession(true);
		this.client = new MqttClient(broker, clientId, persistence);
		client.setCallback(this);
		client.connect(connOpts);

	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			MessageToRepublish msg = Agent.priorityQueue.dequeue();
			if (msg != null) {
				MqttMessage message = new MqttMessage(msg.getPayload().getBytes());
				message.setQos(1);
				try {
					client.publish(msg.getTopic(), message);
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void connectionLost(final Throwable cause) {
		if (Log.ON) {
			PRIORITY.warn("{}", () -> "agent's republishing client lost connection to the broker");
		}
	}

	@Override
	public void messageArrived(final String topic, final MqttMessage message) throws Exception {
		// FIXME
	}

	@Override
	public void deliveryComplete(final IMqttDeliveryToken token) {
		// FIXME
	}
}
