package planemqx.clients;

import static planemqx.common.Log.GEN;

import java.util.List;

import planemqx.common.Log;

/**
 * this class contains the logic of a publisher broker client.
 *
 */
public class Device extends BrokerClient {
	/**
	 * publish frequency.
	 */
	private Integer publishFrequency;
	/**
	 * 
	 */
	private Long messageSize;

	public Device(final String deviceId, final String deviceName, final Integer publishFrequency,
			final Long messageSize, final List<String> topics) {
		super(deviceId, deviceName, topics);
		if (publishFrequency < 0) { throw new IllegalArgumentException("Publishing frequency cannot be < 0"); }
		if (messageSize < 0) { throw new IllegalArgumentException("Message size cannot be < 0"); }
		this.publishFrequency = publishFrequency;
		this.messageSize = messageSize;
		if (Log.ON) {
			GEN.info("{}", () -> this.toString());
		}
	}

	@Override
	public String toString() {
		return "Device " + super.toString() + " [ publishFrequency=" + publishFrequency + ", messageSize=" + messageSize
				+ "]";
	}
}
