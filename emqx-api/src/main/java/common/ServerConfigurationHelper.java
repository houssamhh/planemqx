package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfigurationHelper {

	public static final String LOGIN;
	public static final String PASSWORD;
	public static final String BORKER_URL;

	static {
		Properties props = new Properties();
		String fileName = "config/config.properties";
		try (FileInputStream fis = new FileInputStream(fileName)) {
			props.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		BORKER_URL = props.getProperty("brokerAPIsUrl");
		LOGIN = props.getProperty("login");
		PASSWORD = props.getProperty("password");
	}

}
