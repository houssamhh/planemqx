package clients;

import static common.Log.GEN;

import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import common.Log;
import common.QoS;

/**
 *  this class contains the logic of a publisher broker client.
 *
 */
public class Device extends BrokerClient {

	/**
	 * publish frequency.
	 */
	private Double publishFrequency;

	/**
	 * 
	 */
	private Integer messageSize;
	
	/**
	 * 
	 */
	public Integer getMessageSize() {
		return this.messageSize;
	}
	
	public Double getPublishFrequency() {
		return this.publishFrequency;
	}

	
	/**
	 * @param deviceId
	 * @param deviceName
	 * @param publishFrequency
	 * @param messageSize
	 * @param topics
	 */
	public Device(final String deviceId, final String deviceName, final Double publishFrequency,
			final Integer messageSize, final List<String> topics) {
		super(deviceId, deviceName, topics);
		if (publishFrequency < 0 || messageSize < 0) {
			throw new IllegalArgumentException();
		}
		this.publishFrequency = publishFrequency;
		this.messageSize = messageSize;
		if (Log.ON) { 
			GEN.info("{}", () -> this.toString());
		}
	}

	/**
	 * @param content
	 * @param qos
	 */
	public void publishMessage(final String content, final QoS qos) {
		MqttMessage message = new MqttMessage(content.getBytes());
		message.setQos(qos.getValue());
		topics.stream().forEach(topic -> {
				try {
					mqttClient.publish(topic, message);
					if (Log.ON) {
						GEN.info("{}", () -> clientId + " published " + content + " to topic " + topic);
					}
				} catch (MqttException e) {
					e.printStackTrace();
				}
		});
	}

	@Override
	public String toString() {
		return "Device " + super.toString() + " [ publishFrequency=" + publishFrequency + ", messageSize=" + messageSize
				+ "]";
	}
}