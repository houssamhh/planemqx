#!/usr/bin/python

import time

from mininet.net import Containernet
from mininet.node import Controller
from mininet.cli import CLI
from mininet.link import TCLink
from mininet.log import info, setLogLevel

setLogLevel('info')

network = Containernet(controller=Controller)

info('*** Adding controller\n')
network.addController('c0')

info('*** Adding docker containers\n')

brokerDocker = network.addDocker(
	'broker',
	ip='10.0.0.252',
	dimage='emqx-experiments:1.0.0',
	dcmd="/opt/emqx/bin/emqx foreground",
	ports=[1883, 18083, 8883, 8083, 8084, 8780],
	port_bindings={1883: 1883, 18083: 18083, 8883: 8883, 8083: 8083, 8084: 8084, 8780: 8780}
)

publisherDocker = network.addDocker(
	'publishers',
	ip='10.0.0.251',
	dimage="planemqx-publishers:latest",
	#dcmd="java -jar mqtt-publisher/mqtt-publisher.jar",
	#dcmd = "java -jar ./mqtt-publisher.jar",
	ports=[1883],
	ports_binding={1883: 1883},
	environment={"url": "tcp://10.0.0.252:1883", "topic": "my-custom-topic", "count": "50", "interval": "1", "size": "50000"}
)

subscriberDocker = network.addDocker(
	'subscribers',
	ip='10.0.0.253',
	dimage="planemqx-subscribers:latest",
	#dcmd="java -jar /mqtt-subscriber/mqtt-subscriber.jar",
	ports=[1883],
	ports_binding={1883: 1883},
	environment={"url": "tcp://10.0.0.252:1883", "topic": "my-custom-topic", "count": "50"}
)

apiDocker = network.addDocker(
	'api',
	ip='10.0.0.254',
	dimage="emqx-api:latest",
	ports=[8780, 8080],
	ports_binding={8780: 8780, 8080: 8080}
)

agentDocker = network.addDocker(
	'agent',
	ip='10.0.0.255',
	dimage="planemqx-agent:latest",
	#dcmd="java -jar /mqtt-subscriber/mqtt-subscriber.jar",
	ports=[8880],
	ports_binding={8880: 8880}
)

info('*** Adding switches\n')
publisherSwitch = network.addSwitch('s1')
brokerSwitch = network.addSwitch('s2')
subscriberSwitch = network.addSwitch('s3')
apiSwitch = network.addSwitch('s4')
agentSwitch = network.addSwitch('s5')

info('*** Creating links\n')
network.addLink(publisherDocker, publisherSwitch)
network.addLink(publisherSwitch, brokerSwitch)
network.addLink(brokerDocker, brokerSwitch)
network.addLink(subscriberDocker, subscriberSwitch)
network.addLink(brokerSwitch, subscriberSwitch, cls=TCLink, bw=8)		#Change bandwidth here (in Mbps)
network.addLink(brokerSwitch, apiSwitch)
network.addLink(apiDocker, apiSwitch)
network.addLink(brokerSwitch, agentSwitch)
network.addLink(agentDocker, agentSwitch)

info('*** Starting network\n')
network.start()

# info('*** Testing connectivity\n')
network.ping([publisherDocker, brokerDocker])
network.ping([brokerDocker, subscriberDocker])
network.ping([publisherDocker, subscriberDocker])
network.ping([publisherDocker, agentDocker])
network.ping([brokerDocker, agentDocker])
network.ping([subscriberDocker, agentDocker])
network.ping([publisherDocker, apiDocker])
network.ping([brokerDocker, apiDocker])
network.ping([subscriberDocker, apiDocker])



info('*** Setting up experiment')
#apiDocker.cmd('java -jar target/planiot-0.0.1-SNAPSHOT.jar &')
time.sleep(5)
#agentDocker.cmd('java -jar agent.jar &')
#subscriberDocker.cmd('xterm &')
#publisherDocker.cmd('xterm &')
info('*** Running CLI\n')
CLI(network)
#time.sleep(60)

info('*** Stopping network')
network.stop()
