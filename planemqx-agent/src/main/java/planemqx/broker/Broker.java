package planemqx.broker;

import static planemqx.common.Log.COMM;
import static planemqx.common.Log.STAT;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;

import planemqx.common.APIConsumerHelper;
import planemqx.common.JSONDataParser;
import planemqx.common.Log;
import planemqx.common.Pair;
import planemqx.common.RequestType;

public class Broker {
	private String brokerUrl;
	private String brokerAPIsUrl;
	private String login;
	private String password;
	private String serverUrl;
	private int maxInflight;
	private List<String> rules;

	public Broker(final String brokerUrl, final String brokerAPIsUrl, final String login, final String password,
			final String serverUrl) {
		if (brokerUrl == null || brokerUrl.isBlank()) {
			throw new IllegalArgumentException("Broker URL cannot be null or blank");
		}
		if (brokerAPIsUrl == null || brokerAPIsUrl.isBlank()) {
			throw new IllegalArgumentException("Broker APIs URL cannot be null or blank");
		}
		if (login == null || login.isBlank()) {
			throw new IllegalArgumentException("Login cannot be null or blank");
		}
		if (password == null || password.isBlank()) {
			throw new IllegalArgumentException("Password cannot be null or blank");
		}
		if (serverUrl == null || serverUrl.isBlank()) {
			throw new IllegalArgumentException("Server Url cannot be null or blank");
		}
		this.brokerUrl = brokerUrl;
		this.brokerAPIsUrl = brokerAPIsUrl;
		this.login = login;
		this.password = password;
		this.serverUrl = serverUrl;
		this.maxInflight = 0;
		this.rules = new ArrayList<>();
	}

	
	// doesn't work if QoS = 0, packet will have an id = 0 in that case
	public void forkWithDropping(final String topic, final List<String> clientids, final Double threshold) {
		if (topic == null || topic.isBlank() || clientids == null || clientids.isEmpty()
				|| (threshold != null && (threshold < 0 || threshold > 1))) {
			throw new IllegalArgumentException();
		}
		this.configureSubscription(topic, clientids);
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql;
		/*
		 * 2 loops in order to store apps' dropRates
		 */
		if (threshold == null) {
			sql = "SELECT * FROM \\\"" + topic + "\\\" WHERE payload != 'unsubscribe'";
		} else {
			String thresholdStr = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)
					.multiply(new BigInteger(Double.valueOf((1 - threshold) * 100).intValue() + ""))
					.divide(BigInteger.valueOf(100L)).toString(16);
			sql = "SELECT qos, payload, hexstr2bin(sha256(id)) < hexstr2bin('" + thresholdStr
					+ "') as republish FROM \\\"" + topic + "\\\" WHERE republish = true and payload != 'unsubscribe'";
		}
		parameters.put("sql", "\"" + sql + "\"");
		// actions
		// description
		StringBuilder description = new StringBuilder();
		StringBuilder actions = new StringBuilder("[");
		for (String id : clientids) {
			description.append(id + ", ");
			actions.append("{ \"function\": \"republish\", \"args\": { \"topic\": \"" + topic + "/" + id
					+ /* "\"} }, "; */ "/unsorted\"} }, ");
		}
		String a = actions.substring(0, actions.length() - 2) + "]";
		parameters.put("actions", a);
		String d = description.substring(0, description.length() - 1) + " : "
				+ (threshold == null ? 1 : (1 - threshold));
		parameters.put("description", "\"" + d + "\"");
		try {
			String res = APIConsumerHelper.request(brokerAPIsUrl + "/rules", parameters, login, password,
					RequestType.POST);
			if (Log.ON) {
				COMM.info("{}", () -> res);
			}
			if (res != null && threshold != null) {
				rules.add((String) new JSONObject(res).get("id"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void fork(final String topic, final List<String> clientids) {
		this.forkWithDropping(topic, clientids, null);
	}

	public void forkWithoutDropping(final String topic, final List<String> clientids) {
		this.forkAllWithDropping(topic, clientids, null);
	}

	public void forkAllWithDropping(final String topic, final List<String> clientids, final Double threshold) {
		if (topic == null || topic.isBlank() || clientids == null || clientids.isEmpty()
				|| (threshold != null && (threshold < 0 || threshold > 1))) {
			throw new IllegalArgumentException();
		}
		this.configureSubscription(topic, clientids);
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql;
		/*
		 * 2 loops in order to store apps' dropRates
		 */
		if (threshold == null) {
			sql = "SELECT * FROM \\\"" + topic + "\\\" WHERE payload != 'unsubscribe'";
		} else {
			BigInteger temp = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)
					.multiply(new BigInteger(Double.valueOf((1 - threshold) * 100).intValue() + ""))
					.divide(BigInteger.valueOf(100L));
			String thresholdStr = temp.toString(16);
			sql = "SELECT qos, payload, hexstr2bin(sha256(id)) < hexstr2bin('" + thresholdStr
					+ "') as republish FROM \\\"" + topic + "\\\" WHERE republish = true and payload != 'unsubscribe'";
		}
		parameters.put("sql", "\"" + sql + "\"");
		// actions
		// description
		StringBuilder description = new StringBuilder();
		StringBuilder actions = new StringBuilder("[");
		for (String id : clientids) {
			description.append(id + ", ");
			actions.append("{ \"function\": \"republish\", \"args\": { \"topic\": \"" + topic + "/" + id + "\"} }, ");
		}
		String a = actions.substring(0, actions.length() - 2) + "]";
		parameters.put("actions", a);
		String d = description.substring(0, description.length() - 1) + " : "
				+ (threshold == null ? 1 : (1 - threshold));
		parameters.put("description", "\"" + d + "\"");
		try {
			String res = APIConsumerHelper.request(brokerAPIsUrl + "/rules", parameters, login, password,
					RequestType.POST);
			if (Log.ON) {
				COMM.info("{}", () -> res);
			}
			if (res != null && threshold != null) {
				rules.add((String) new JSONObject(res).get("id"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void configureSubscription(final String source, final List<String> clientids) {
		if (source == null || source.isBlank() || clientids.isEmpty()) {
			throw new IllegalArgumentException();
		}
		// subscribe/unsubscribe action config
		clientids.forEach(clientid -> {
			Map<String, Object> serverParameters = new HashMap<>();
			serverParameters.put("topic", "\"" + source + "\"");
			serverParameters.put("rewrited_to", "\"" + source + "/" + clientid + "\"");
			serverParameters.put("clientid", "\"" + clientid + "\"");
			try {
				APIConsumerHelper.request(serverUrl, serverParameters, null, null, RequestType.POST);
			} catch (IOException e) {
				COMM.error("{}", () -> serverUrl);
				e.printStackTrace();
			}

		});
	}

	public void configureTopicPriorities(final Map<String, Integer> parameters) {
		String property = "mqtt.mqueue_priorities";
		if (parameters == null || parameters.isEmpty()) {
			return;
		}
		try {
			String str = APIConsumerHelper.request(brokerAPIsUrl + "/configs/global_zone", "{}", login, password,
					RequestType.GET);
			Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(str);
			// remove configured topic prioritization
			flattenedJsonMap.entrySet().removeIf(e -> e.getKey().length() >= property.length()
					&& e.getKey().substring(0, property.length()).equals(property));
			// configure the new one
			parameters.entrySet().forEach(e -> {
				if (e.getValue() > 0 && e.getValue() < 256) {
					flattenedJsonMap.put(property + "." + e.getKey(), e.getValue());
				}
			});
			String nestedJson = JsonUnflattener.unflatten(flattenedJsonMap.toString());
			APIConsumerHelper.request(brokerAPIsUrl + "/configs/global_zone", nestedJson, login, password,
					RequestType.PUT);
			if (Log.ON) {
				COMM.info("{}", () -> "success: topic prioritization config");
			}
		} catch (IOException e) {
			if (Log.ON) {
				COMM.error("{}", () -> "topic prioritization config");
			}
			e.printStackTrace();
		}
	}

	public void configureCapacity(final int capacity) {
		if (capacity < 0) {
			throw new IllegalArgumentException();
		}
		try {
			String str = APIConsumerHelper.request(brokerAPIsUrl + "/configs/global_zone", "{}", login, password,
					RequestType.GET);
			Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(str);
			this.maxInflight = capacity;
			flattenedJsonMap.put("mqtt.max_mqueue_len", capacity);
			String nestedJson = JsonUnflattener.unflatten(flattenedJsonMap.toString());
			APIConsumerHelper.request(brokerAPIsUrl + "/configs/global_zone", nestedJson, login, password,
					RequestType.PUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cleanRules() {
		try {
			JSONDataParser
					.getRulesIds(
							APIConsumerHelper.request(brokerAPIsUrl + "/rules", "{}", login, password, RequestType.GET))
					.forEach(id -> {
						try {
							APIConsumerHelper.request(brokerAPIsUrl + "/rules/" + id, "{}", login, password,
									RequestType.DELETE);
						} catch (IOException e) {
							if (Log.ON) {
								COMM.error("{}", () -> "delete rule " + id);
							}
						}
					});
			final String str = "{ \"actions\": [ \"webhook:unsubscription\" ], \"id\": \"redirect_unsubscibe_message\", \"sql\": \"SELECT * FROM \\\"#\\\" WHERE payload = 'unsubscribe'\" }";
			APIConsumerHelper.request(brokerAPIsUrl + "/rules", str, login, password, RequestType.POST);
		} catch (IOException e) {
			if (Log.ON) {
				COMM.error("{}", () -> "get rules informations");
			}
		}
	}

	public Pair<Integer, Integer> getRulesStat(final Double delta) {
		int success = 0;
		int total = 0;
		for (String rule : rules) {
			try {
				String log = "";
				String res = APIConsumerHelper.request(brokerAPIsUrl + "/rules/" + rule, "{}", login, password,
						RequestType.GET);
				Map<String, Object> flattenedJsonMap = JsonFlattener.flattenAsMap(res);
				String topic = (String) flattenedJsonMap.get("from[0]");
				// TODO form is a []
				// get length of []
				// for i: from[i]
				Double matched = ((BigDecimal) flattenedJsonMap.get("metrics.matched")).doubleValue();
				Double passed = ((BigDecimal) flattenedJsonMap.get("metrics.passed")).doubleValue();
				String description = (String) flattenedJsonMap.get("description");
				Double threshold = Double.parseDouble(description.split(":")[1].strip());
				log += topic + " -> " + description + " => dropping  = " + passed + " / " + matched + " = ";
				if (matched != 0) {
					Double c = Math.round(passed / matched * 10000) / 10000.0;
					log += c + " => delta " + Math.abs(threshold - c) + " => "
							+ (Math.abs(threshold - c) <= delta ? "OK" : "KO");
					if (c < 1) {
						total++;
						if (Math.abs(threshold - c) <= delta) {
							success++;
						}
					}
				} else {
					log += "--";
				}
				String logStat = log;
				if (Log.ON) {
					STAT.info("{}", () -> logStat);
				}
			} catch (IOException e) {
				if (Log.ON) {
					STAT.info("{}", () -> "error");
				}
			}
		}
		return Pair.of(success, total);
	}

	public String getBrokerUrl() {
		return brokerUrl;
	}

	@Override
	public String toString() {
		return "Broker [brokerUrl=" + brokerUrl + ", brokerAPIsUrl=" + brokerAPIsUrl + ", login=" + login
				+ ", password=" + password + ", serverUrl=" + serverUrl + ", maxInflight=" + maxInflight + "]";
	}

}