package planemqx.common;

import static planemqx.common.Log.GEN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import planemqx.clients.ApplicationCategory;

public class AIDataParser {

	private AIDataParser() {
	}

	public static Pair<Map<ApplicationCategory, Double>, List<ApplicationCategory>> readData(final String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			Map<ApplicationCategory, Double> dropping = new HashMap<>();
			List<ApplicationCategory> priorities = new ArrayList<>();
			String line = br.readLine();
			Arrays.asList(line.split(":")[1].split(" ")[1].substring(9).split("_")).forEach(token -> {
				ApplicationCategory category = ApplicationCategory.fromValue(token.substring(0, 2).toLowerCase());
				if (category != null) {
					dropping.put(category, Double.valueOf(token.substring(2)));
				}
			});
			line = br.readLine();
			Arrays.asList(line.split(":")[1].split(" ")[1].substring(11).split("_")).forEach(token -> {
				ApplicationCategory category = ApplicationCategory.fromValue(token.toLowerCase());
				if (category != null) {
					priorities.add(category);
				}
			});
			return Pair.of(dropping, priorities);
		} catch (Exception e) {
			e.printStackTrace();
			if (Log.ON)
				GEN.error("{}", () -> "erros while parsing AIPlanner File");
			return null;
		}
	}
}
