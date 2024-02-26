package clients;

import static common.Log.COMM;
import static common.Log.STAT;

import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import common.Log;

public class OnMessageCallback implements MqttCallback {

	/**
	 *
	 */
	public void connectionLost(Throwable cause) {
		cause.printStackTrace();
		if (Log.ON) {
			COMM.info("{}", () -> "disconnected, you can reconnect ");
		}
	}

	/**
	 *
	 */
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if (Log.ON) {
			COMM.info("{}", () -> {
				return "received message [ " + message.getId() + ", " + topic + ", " + new String(message.getPayload())
						+ ", " + message.getQos() + "]";
			});
		}
		if (Log.ON) {
			try {
				Long time = Long.parseLong((new String(message.getPayload())));
				STAT.info("{}", () -> {
					return "Received [" + topic + ", " + (System.currentTimeMillis() - time) + "]";
				});
			} catch (NumberFormatException e) {
			}
		}
	}

	/**
	 *
	 */
	public void deliveryComplete(IMqttDeliveryToken token) {
		if (Log.ON) {
			COMM.info("{}", () -> {
				return "Published message [ " + token.getMessageId() + ", " + token.getClient().getClientId() + ", "
						+ Arrays.asList(token.getTopics()).toString();
			});
		}
	}

}