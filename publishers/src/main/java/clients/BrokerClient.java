package clients;

import static common.Log.GEN;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import common.Configuration;
import common.Log;
import common.PasswordGenerator;

/**
 * this class contains the logic of a broker client.
 *
 */
public abstract class BrokerClient {

	/**
	 * client identifier.
	 */
	protected String clientId;

	/**
	 * client name.
	 */
	protected String clientName;

	/**
	 * client password.
	 */
	protected String clientPwd;

	/**
	 * client topics.
	 */
	protected List<String> topics;

	/**
	 * message queueing ter client used to communicate with a broker.
	 */
	protected MqttClient mqttClient;

	/**
	 * constructor.
	 * 
	 * @param clientId
	 * @param clientName
	 * @param topics
	 */
	public BrokerClient(final String clientId, final String clientName, final List<String> topics) {
		if (clientId == null) { throw new IllegalArgumentException("MQTT Client ID cannot be null"); }
		if (clientId.isBlank()) { throw new IllegalArgumentException("MQTT Client ID cannot be blank"); }
		if (clientName == null) { throw new IllegalArgumentException("MQTT Client name cannot be null"); }
		if (clientName.isBlank()) { throw new IllegalArgumentException("MQTT Client name cannot be blank"); }

		this.clientId = clientId;
		this.clientName = clientName;
		this.clientPwd = PasswordGenerator.generatePassword(20);
		this.topics = new ArrayList<String>(topics);
	}

	/**
	 * @param brokerUrl
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void connect(final String brokerUrl) throws IOException {
		try {
			mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setUserName(Configuration.AGENT_NAME);
			connOpts.setPassword(Configuration.AGENT_PASSWORD.toCharArray());
			connOpts.setCleanSession(true);
			connOpts.setMaxInflight(65535);	
			mqttClient.setCallback(new OnMessageCallback());
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

	/**
	 * disconnect the client from the broker.
	 */
	public void disconnect() {
		try {
			mqttClient.disconnect();
			if (Log.ON) {
				GEN.info("{}", () -> clientId + ": disconnected");
			}
		} catch (MqttException e) {
			if (Log.ON) {
				GEN.error("{}", () -> clientId + ": could not disconnect");
			}
			e.printStackTrace();
		}
	}

	/**
	 * close connection with the broker.
	 */
	public void closeConnection() {
		try {
			mqttClient.close();
			if (Log.ON) {
				GEN.info("{}", () -> clientId + ": connection closed");
			}
		} catch (MqttException e) {
			if (Log.ON) {
				GEN.error("{}", () -> clientId + ": could not close the connection to the broker");
			}
			e.printStackTrace();
		}
	}

	/**
	 * @return boolean that represents connection state with the broker.
	 */
	public boolean isConnectedTo() {
		return mqttClient.isConnected();
	}

	public String getClientId() {
		return clientId;
	}

	public List<String> getTopics() {
		return new ArrayList<String>(topics);
	}
	
	// TODO: remove

	public MqttClient getMqttClient() {
		return mqttClient;
	}

	public void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

	@Override
	public String toString() {
		return "BrokerClient [clientId=" + clientId + ", clientName=" + clientName + ", topics=" + topics
				+ ", mqttClient=" + (mqttClient != null ? mqttClient.getServerURI() : null) + "]";
	}

}