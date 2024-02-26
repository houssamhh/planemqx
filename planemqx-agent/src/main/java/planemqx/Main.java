package planemqx;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;

import planemqx.agent.Agent;
import planemqx.commonPriorities.APIServices;

public class Main {
	public static void main(final String[] args) throws MqttException, IOException, InterruptedException {
		Thread.sleep(5000);
		APIServices apiServices = new APIServices();
		apiServices.addAgentUser();
		Agent agent = new Agent();
		agent.start();
		System.out.println("PlanEMQX Agent Activated!");
		Thread.sleep(10000000);
		System.out.println("PlanEMQX Agent  Deactivated");
		System.exit(0);
	}
}
