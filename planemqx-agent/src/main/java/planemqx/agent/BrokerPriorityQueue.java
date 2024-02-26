package planemqx.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class BrokerPriorityQueue<T> {

	private final int priorities;
	private final int size;
	private final List<Queue<MessageToRepublish>> priorityQueues;

	public BrokerPriorityQueue(final int priorities) {
		this(priorities, 1000);
	}

	public BrokerPriorityQueue(final int priorities, int size) {
		this.priorities = priorities;
		this.size = size;
		this.priorityQueues = new ArrayList<>();
		initializePriorityQueues();
	}

	private void initializePriorityQueues() {
		for (int i = 0; i < priorities; i++) {
			priorityQueues.add(new ArrayBlockingQueue<>(size));
		}
	}

	public void enqueue(final int priority, final MessageToRepublish message) {
		priorityQueues.get(priority).add(message);
	}

	public MessageToRepublish dequeue() {
		for (int priority = 0; priority < priorities; priority++) {
			Queue<MessageToRepublish> queue = priorityQueues.get(priority);
			MessageToRepublish object = queue.poll();
			if (Objects.nonNull(object)) {
				return object;
			}
		}
		return null;
	}
}
