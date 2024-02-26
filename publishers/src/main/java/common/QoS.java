package common;

/**
 * @author hp
 *
 */
public enum QoS {
	
	/**
	 * 
	 */
	AT_MOST_ONCE (0),
	
	/**
	 * 
	 */
	AT_LEAST_ONCE (1),
	
	/**
	 * 
	 */
	EXACTLY_ONCE (2);
	
	
	/**
	 * 
	 */
	private Integer value;
	
	
	/**
	 * @param qos
	 */
	private QoS(final int value){
		this.value = value;
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static QoS fromValue(int value) {
		for(QoS v: QoS.values()) {
			if(v.value == value) {
				return v;
			}
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return "" + value;
	}
}
