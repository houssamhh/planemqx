package planemqx.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import planemqx.clients.Application;
import planemqx.clients.ApplicationCategory;
import planemqx.clients.Device;

public class JSONDataParser {
	
	private JSONDataParser() {
	}

	public static Map<String, Device> getDevices(final String jsonData) {
		JSONObject json = new JSONObject(jsonData);
		HashMap<String, Device> devices = new HashMap<>();
		for (Object obj : json.getJSONArray("IoTdevices")) {
			JSONObject jsonObj = new JSONObject(obj.toString());
			List<String> publishesTo = new ArrayList<>();
			jsonObj.getJSONArray("publishesTo").forEach(topic -> publishesTo.add(String.valueOf(topic)));
			String id = jsonObj.getString("deviceId");
			Device device = new Device(id, jsonObj.getString("deviceName"), jsonObj.getInt("publishFrequency"),
					jsonObj.getLong("messageSize"), publishesTo);
			devices.put(jsonObj.getString("deviceId"), device);
		}
		return devices;
	}

	public static HashMap<String, Application> getApplications(final String jsonData) {
		JSONObject json = new JSONObject(jsonData);
		HashMap<String, Application> applications = new HashMap<>();
		for (Object obj : json.getJSONArray("applications")) {
			JSONObject jsonObj = new JSONObject(obj.toString());
			List<String> subscribeTo = new ArrayList<>();
			jsonObj.getJSONArray("subscribesTo").forEach(topic -> subscribeTo.add(String.valueOf(topic)));
			Application application = new Application(jsonObj.getString("applicationId"),
					jsonObj.getString("applicationName"), jsonObj.getInt("priority"),
					jsonObj.getEnum(ApplicationCategory.class, "applicationCategory"), subscribeTo);
			applications.put(jsonObj.getString("applicationId"), application);
		}
		return applications;
	}

	public static Map<String, List<String>> getTopicsDetails(final String jsonData) {
		JSONObject json = new JSONObject(jsonData);
		Map<String, List<String>> topics = new HashMap<>();
		for (Object obj : json.getJSONArray("topics")) {
			JSONObject jsonObj = new JSONObject(obj.toString());
			List<String> subscribers = new ArrayList<>();
			jsonObj.getJSONArray("subscribers").forEach(sub -> subscribers.add(String.valueOf(sub)));
			topics.put(jsonObj.getString("topicId"), subscribers);
		}
		return topics;
	}

	public static List<String> getRulesIds(final String jsonData) {
		try {
			Map<String, Object> response = new ObjectMapper().readValue(jsonData, new TypeReference<>() {
			});
			List<Map<String, Object>> rules = (List<Map<String, Object>>) response.get("data");
			return rules.stream().map(rule -> (String) rule.get("id")).collect(Collectors.toList());
		} catch (Exception exception) {
			return new ArrayList<>();
		}
	}
}
