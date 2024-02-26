package planemqx.clients;

import static planemqx.common.Log.COMM;
import static planemqx.common.Log.STAT;

import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import planemqx.common.Log;

public class OnMessageCallback implements MqttCallback {

	public void connectionLost(final Throwable cause) {
		cause.printStackTrace();
		if (Log.ON) {
			COMM.info("{}", () -> "disconnected, you can reconnect ");
		}
	}

	public void messageArrived(final String topic, final MqttMessage message) throws Exception {
		if (Log.ON) {
			COMM.info("{}", () -> {
				return "received message [ " + message.getId() + ", " + topic + ", " + new String(message.getPayload())
						+ ", " + message.getQos() + "]";
			});
		}
		if (Log.ON) {
			try {
				Long time = Long.parseLong((new String(message.getPayload())));
				STAT.info("{}", () -> "Received [" + topic + ", " + (System.currentTimeMillis() - time) + "]");
			} catch (NumberFormatException e) {
			}
		}
	}

	public void deliveryComplete(final IMqttDeliveryToken token) {
		if (Log.ON) {
			COMM.info("{}", () -> "Published message [ " + token.getMessageId() + ", " + token.getClient().getClientId()
					+ ", " + Arrays.asList(token.getTopics()).toString());
		}
	}
}
