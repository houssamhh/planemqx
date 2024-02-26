package common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import clients.Device;

/**
 * @author hp
 *
 */
public class JSONDataParser {

	/**
	 * @param jsonData
	 * @return
	 */
	public static HashMap<String, Device> getDevices(final String jsonData) {

		JSONObject json = new JSONObject(jsonData);

		HashMap<String, Device> devices = new HashMap<String, Device>();
		for (Object obj : json.getJSONArray("IoTdevices")) {
			JSONObject jsonObj = new JSONObject(obj.toString());

			List<String> publishesTo = new ArrayList<String>();
			jsonObj.getJSONArray("publishesTo").forEach(topic -> publishesTo.add(String.valueOf(topic)));

			String id = jsonObj.getString("deviceId");

			Device device = new Device(id, jsonObj.getString("deviceName"), jsonObj.getDouble("publishFrequency"),
					jsonObj.getInt("messageSize"), publishesTo);

			devices.put(jsonObj.getString("deviceId"), device);
		}
		return devices;
	}

}
