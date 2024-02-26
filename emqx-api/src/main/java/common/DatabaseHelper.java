package common;

import static common.Log.GEN;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {

	private static Connection connection;

	public static Map<String, String> mapping;

	// problem with QoS
	// init block
	static {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:test.db");
			Statement stmt = connection.createStatement();
//			String temSql = "DROP TABLE MAPPING";
//			stmt.executeUpdate(temSql);
			String sql = "CREATE TABLE MAPPING (CLIENTID TEXT NOT NULL," + " ORIGINAL_TOPIC TEXT NOT NULL, "
					+ "SUBTOPIC TEXT NOT NULL, PRIMARY KEY (CLIENTID, ORIGINAL_TOPIC) )";
			stmt.executeUpdate(sql);
			ResultSet rs = stmt.executeQuery("SELECT * FROM MAPPING");
			mapping = new HashMap<>();
			while (rs.next()) {
				mapping.put(rs.getString("CLIENTID") + rs.getString("ORIGINAL_TOPIC"), rs.getString("SUBTOPIC"));
			}
			if (Log.ON) {
				GEN.info("{}", () -> "save authentification to the db");
			}
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static synchronized int insert(String originalTopic, String rewritedTopic, String clientId) {
		try (Statement stmt = connection.createStatement()) {
			String sql = "INSERT INTO MAPPING (CLIENTID, ORIGINAL_TOPIC, SUBTOPIC) " + "VALUES ('" + clientId + "', '"
					+ originalTopic + "', '" + rewritedTopic + "')";
			if (stmt.executeUpdate(sql) == 1) {
				mapping.put(clientId + originalTopic, rewritedTopic);
				if (Log.ON) {
					GEN.info("{}", () -> mapping.toString());
				}
				return 1;
			}
			return 0;
		} catch (SQLException e) {
			// case the unique constraint in the primary key is not respected
			e.printStackTrace();
			return 0;
		}
	}

	public static synchronized int delete(String originalTopic, String rewritedTopic, String clientId) {
		try (Statement stmt = connection.createStatement()) {
			String sql = "DELETE FROM MAPPING WHERE CLIENTID = " + clientId + " and ORIGINAL_TOPIC = " + originalTopic;
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static Map<String, String> select() {
		return mapping;
	}

	public static synchronized String getById(String clientId, String originalTopic) {
		try (Statement stmt = connection.createStatement()) {
			String sql = "SELECT SUBTOPIC FROM MAPPING WHERE CLIENTID ='" + clientId + "' and ORIGINAL_TOPIC ='"
					+ originalTopic + "'";
			return stmt.executeQuery(sql).getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized HashMap<String, String> getAllByOriginalTopic(String originalTopic) {
		HashMap<String, String> applicationsAndRewritedTopic = new HashMap<String, String>();
		try (Statement stmt = connection.createStatement()) {
			String sql = "SELECT CLIENTID,SUBTOPIC FROM MAPPING WHERE ORIGINAL_TOPIC ='" + originalTopic + "'";
			ResultSet resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				applicationsAndRewritedTopic.put(resultSet.getString("CLIENTID"), resultSet.getString("SUBTOPIC"));
			}
			return applicationsAndRewritedTopic;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
