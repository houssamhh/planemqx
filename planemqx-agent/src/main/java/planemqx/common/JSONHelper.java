package planemqx.common;

import java.util.Map;

public class JSONHelper {
	
	private JSONHelper() {
	}

	public static String parseToString(final Map<String, Object> map) {
		if (map == null) {
			return "{}";
		}
		StringBuilder res = new StringBuilder("{");
		for (Map.Entry<String, Object> e : map.entrySet()) {
			if (e.getValue() != null && !e.getValue().toString().isBlank()) {
				res.append("\"" + e.getKey() + "\"" + ": " + e.getValue().toString() + ", ");
			}
		}
		return res.substring(0, res.length() - 2) + "}";
	}
}
