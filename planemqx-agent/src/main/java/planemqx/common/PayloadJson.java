package planemqx.common;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;
import planemqx.commonPriorities.Application;
import planemqx.commonPriorities.ApplicationCategory;
import planemqx.commonPriorities.IoTdevice;
import planemqx.commonPriorities.MappingBroker;
import planemqx.commonPriorities.Topic;

@Data
@JsonPropertyOrder({ "IoTdevices", "virtualSensors", "actuators", "applications", "applicationCategories", "topics",
		"broker", "systemBandwidth", "bandwidthPolicy", "commChannelLossAN", "commChannelLossRT", "commChannelLossTS",
		"commChannelLossVS", "brokerCapacity" })
public class PayloadJson {
	@JsonProperty("IoTdevices")
	private List<IoTdevice> ioTdevices = null;
	@JsonProperty("virtualSensors")
	private List<Object> virtualSensors = null;
	@JsonProperty("actuators")
	private List<Object> actuators = null;
	@JsonProperty("applications")
	List<Application> applications = null;
	@JsonProperty("applicationCategories")
	private List<ApplicationCategory> applicationCategories = null;
	@JsonProperty("topics")
	private List<Topic> topics = null;
	@JsonProperty("broker")
	private List<MappingBroker> mappingBroker = null;
	@JsonProperty("systemBandwidth")
	private int systemBandwidth;
	@JsonProperty("bandwidthPolicy")
	private String bandwidthPolicy;
	@JsonProperty("commChannelLossAN")
	private int commChannelLossAN;
	@JsonProperty("commChannelLossRT")
	private int commChannelLossRT;
	@JsonProperty("commChannelLossTS")
	private int commChannelLossTS;
	@JsonProperty("commChannelLossVS")
	private int commChannelLossVS;
	@JsonProperty("brokerCapacity")
	private int brokerCapacity;

	/**
	 * No args constructor for use in serialization
	 *
	 */
	public PayloadJson() {
	}

	public PayloadJson(final List<IoTdevice> ioTdevices, final List<Object> virtualSensors,
			final List<Object> actuators, final List<Application> applications,
			final List<ApplicationCategory> applicationCategories, final List<Topic> topics,
			final List<MappingBroker> mappingBroker, final int systemBandwidth, final String bandwidthPolicy,
			final int commChannelLossAN, final int commChannelLossRT, final int commChannelLossTS,
			final int commChannelLossVS, final int brokerCapacity) {
		super();
		this.ioTdevices = ioTdevices;
		this.virtualSensors = virtualSensors;
		this.actuators = actuators;
		this.applications = applications;
		this.applicationCategories = applicationCategories;
		this.topics = topics;
		this.mappingBroker = mappingBroker;
		this.systemBandwidth = systemBandwidth;
		this.bandwidthPolicy = bandwidthPolicy;
		this.commChannelLossAN = commChannelLossAN;
		this.commChannelLossRT = commChannelLossRT;
		this.commChannelLossTS = commChannelLossTS;
		this.commChannelLossVS = commChannelLossVS;
		this.brokerCapacity = brokerCapacity;
	}

	public Optional<Application> getAppById(final String idApp) {
		for (Application app : applications) {
			if (app.getApplicationId().equals(idApp)) {
				return Optional.of(app);
			}
		}
		return Optional.empty();
	}
}
