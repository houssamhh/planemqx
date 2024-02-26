package planemqx.clients;

/**
 * @author hp
 *
 */
public enum ApplicationCategory {
	/**
	 * Realtime sensor
	 */
	RT("rt"),
	/**
	 * Transactional sensor
	 */
	TS("ts"),
	/**
	 * Video streaming sensor
	 */
	VS("vs"),
	/**
	 * Analytic sensor
	 */
	AN("an");

	/**
	 * 
	 */
	private String value;

	/**
	 * @param value
	 */
	private ApplicationCategory(final String value) {
		this.value = value;
	}

	/**
	 * @param value
	 * @return
	 */
	public static ApplicationCategory fromValue(final String value) {
		for (ApplicationCategory v : ApplicationCategory.values()) {
			if (v.value.equals(value)) {
				return v;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
