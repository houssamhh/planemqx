package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

import clients.Application;
import clients.ApplicationCategory;

/**
 * @author hp
 *
 */
public class JSONDataParser {


	/**
	 * @param jsonData
	 * @return
	 */
	public static ConcurrentHashMap<String, Application> getApplications(final String jsonData) {
		JSONObject json = new JSONObject(jsonData);
		ConcurrentHashMap<String, Application> applications = new ConcurrentHashMap<>();
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

}
