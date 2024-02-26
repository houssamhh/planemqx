package planemqx.commonPriorities;

import java.util.List;

public class IoTdevice {
	private String deviceId;
	private String deviceName;
	private int publishFrequency;
	private int messageSize;
	private String distribution;
	private List<String> publishesTo = null;

	/**
	 * No args constructor for use in serialization
	 * 
	 */
	public IoTdevice() {
	}

	public IoTdevice(final String deviceId, final String deviceName, final int publishFrequency, final int messageSize,
			final String distribution, final List<String> publishesTo) {
		super();
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.publishFrequency = publishFrequency;
		this.messageSize = messageSize;
		this.distribution = distribution;
		this.publishesTo = publishesTo;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(final String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(final String deviceName) {
		this.deviceName = deviceName;
	}

	public int getPublishFrequency() {
		return publishFrequency;
	}

	public void setPublishFrequency(final int publishFrequency) {
		this.publishFrequency = publishFrequency;
	}

	public int getMessageSize() {
		return messageSize;
	}

	public void setMessageSize(final int messageSize) {
		this.messageSize = messageSize;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(final String distribution) {
		this.distribution = distribution;
	}

	public List<String> getPublishesTo() {
		return publishesTo;
	}

	public void setPublishesTo(final List<String> publishesTo) {
		this.publishesTo = publishesTo;
	}
}
