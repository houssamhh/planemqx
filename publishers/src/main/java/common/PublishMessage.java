package common;

import static common.Log.STAT;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

import clients.Device;

public class PublishMessage implements Runnable {

	private Device device;
	private int duration;
	private int nbMessagesPublished;

	private static final String DEFAULT_MESSAGE = "{ \"timestamp\": ${TIMESTAMP}, \"padding\": \"${PADDING}\" }";

	public PublishMessage(Device device, int duration) {
		this.device = device;
		this.duration = duration > 0 ? duration : 0;
		this.nbMessagesPublished = 0;
	}

	@Override
	public void run() {
		long t = System.currentTimeMillis();
		long end = t + this.duration;
		if (Log.ON) {
			STAT.info("{}", "[Publisher: " + device.getClientId() + "] Publishing messages for " + duration / 1000
					+ " seconds.");
		}
		while (System.currentTimeMillis() < end) {
			device.publishMessage(createMessage(device.getMessageSize()), QoS.AT_MOST_ONCE);
			Long sleepTime = (long) ((1.0 / device.getPublishFrequency()) * 1000);
			this.nbMessagesPublished++;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // 1/f x 1000
//			device.publishMessage(Long.valueOf(System.currentTimeMillis()).toString(), QoS.AT_MOST_ONCE);
		}
		if (Log.ON) {
			STAT.info("{}", () -> device.getClientId() + " published " + this.nbMessagesPublished + " messages during "
					+ this.duration + " seconds");
		}
		try {
			CSVWriter writer = new CSVWriter(new FileWriter("results/result.csv", true));
			for (String topic : device.getTopics()) {
				String[] data = { device.getClientId(), topic, String.valueOf(this.nbMessagesPublished) };
				writer.writeNext(data);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String createMessage(int size) {
		String message = DEFAULT_MESSAGE.replace("${TIMESTAMP}", String.valueOf(System.currentTimeMillis()));
		int remainingSize = size - (message.length() - "${PADDING}".length());
		message = message.replace("${PADDING}", "x".repeat(Math.max(0, remainingSize)));
		return message;
	}
}
