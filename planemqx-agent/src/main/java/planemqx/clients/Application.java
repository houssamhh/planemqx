package planemqx.clients;

import static planemqx.common.Log.COMM;
import static planemqx.common.Log.GEN;

import java.util.List;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import planemqx.common.Log;

/**
 * This class contains the logic of a subscriber broker client.
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

	public Application(final String applicationId, final String applicationName, final Integer priority,
			final ApplicationCategory category, final List<String> topics) {
		super(applicationId, applicationName, topics);
		if (priority < 0 || priority > 255) {
			throw new IllegalArgumentException();
		}
		this.priority = priority;
		this.category = category;
		if (Log.ON) {
			GEN.info("{}", () -> this.toString());
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
	public void connectionLost(final Throwable cause) {
		// FIXME
	}

	@Override
	public void messageArrived(final String topic, final MqttMessage message) throws Exception {
		if (topic.equals("/unsubscribe")) {
			if (Log.ON) {
				COMM.info("{}", () -> "unsubscription");
			}
		}
		if (Log.ON) {
			COMM.info("{}", () -> "message for topic: " + topic);
		}
	}

	@Override
	public void deliveryComplete(final IMqttDeliveryToken token) {
		// FIXME
	}
}