# PlanEMQX: A Message Broker for Adaptive Data Exchange in the IoT

## Project Description
PlanEMQX is a prototype built on top of the [EMQX](emqx.io) message broker, enabling adaptive data exchange in IoT environments. This is achieved by (i) refining per-subscription data flows based on the applications deployed in the environment, (ii) dynamically assigning drop rates or priorities to data flows according to the requirements of the subscribing applications, and (iii) enabling the adaptation of data flows based on dynamic changes in the environment or evolving applications' requirements. For more information about how PlanEMQX is implemented, please refer to [1].
This repository contains the source code of the PlanEMQX implementation, along with an emulated network environment for using and testing PlanEMQX.

## Getting Started
This repository contains the following directories:
`emqx`: contains the Docker file for building the EMQX Docker image.
`emqx-api`: contains the code for the EMQX API for managing subscription requests.
`experiments`: contains the script needed to run experiments with PlanEMQX, and the results of the experiments conducted in [1].
`planemqx-agent`: contains the code for the PlanEMQX core components (Subscriptions Manager, Message Flow Manager, Drop Rate Manager, Priorities Manager).
`publishers`: contains the code for emulating IoT devices that connect and publish to PlanEMQX.
`subscribers`: contains the code for emulating applications that connect and publish to PlanEMQX.

### Installation Requirements
This artifact has been prepared for a host machine running [Ubuntu 20.04 LTS](https://releases.ubuntu.com/focal/). In addition, you should install [jdk-17](https://www.oracle.com/fr/java/technologies/downloads/#java17), [Maven 3.8.7](https://maven.apache.org/docs/3.8.7/release-notes.html), [Docker](https://docs.docker.com/engine/install/ubuntu/), [Python3](https://www.python.org/downloads/), and [Containernet](https://containernet.github.io/).

**N.B**: when installing *Containernet*, please do so using the Bare-metal installation option (Option 1). Using the nested Docker deployment option (Option 2) may generate errors when running experiments.

## Running Experiments with PlanEMQX
This section provides instructions for running a sample experiment with PlanEMQX given predefined IoT system specifications. To define your own IoT environment and parameters, please refer to [the next section](#configuring-the-iot-system). Results for experiments ran for [1] can be found in [experiments/results](experiments/results/).
### Building Docker Images
To emulate the underlying network infrastructure among different hosts, we use [Containernet](https://containernet.github.io/), a fork of the famous [Mininet](https://mininet.org/) network emulator which allows to use Docker containers as hosts in emulated network topologies.
For this purpose, we build the different components of the IoT system as Docker images. To build the Docker images, start by cloning this repository: 
`$ git clone https://github.com/satrai-lab/planemqx.git`
Then, go into the `planemqx` directory. You can now build the Docker images needed for running the experiments (please make sure that you use the same tags for the images as follows):
- Build the EMQX Docker image: `$ sudo docker build -t emqx-experiments:1.0.0 emqx/.`
- Build the EMQX API Docker image: `$ sudo docker build -t emqx-api emqx-api/.`
- Build the PlanEMQX Agent Docker image: `$ sudo docker build -t planemqx-agent planemqx-agent/.`
- Build the publishers' Docker image: `$ sudo docker build -t planemqx-publishers publishers/.`
- Build the subscribers' Docker image: `$ sudo docker build -t planemqx-subscribers subscribers/.`

You can verify that all images have been correctly built by listing all Docker images: `$ sudo docker image ls`.

### Running Simulations with Containernet
We use the Containernet Python API to create the network topology of our system. The script for defining and running the topology can be found in `experiments/experimental_framework.py`. You can run it with the following command:
`$ sudo python3 experiments/experimental_framework.py`.
Once the script is successfully run, you will be able to see the Containernet CLI. You can then run the different components of PlanEMQX. To do so, run the following commands **from separate terminals**:
- Start the EMQX API:
```
$ sudo docker exec -it mn.api /bin/bash
$ java -jar target/emqx-api-0.0.1-SNAPSHOT.jar
```
- Then launch the PlanEMQX Agent:
```
$ sudo docker exec -it mn.agent /bin/bash
$ java -jar agent.jar
```
- Start the subscribers:
```
$ sudo docker exec -it mn.subscribers /bin/bash
$ java -jar subscribers.jar
```
- And finally start the publishers:
```
$ sudo docker exec -it mn.publishers /bin/bash
$ java -jar publishers.jar
```
The simulation will run for approximately three minutes (you can know when the simulation is done by viewing the display of the subscribers' container).

### Collecting Results
When the simulation is done, metrics will be saved in the subscribers container's `results` directory. The directory will contain two files:
- `response_time.csv`: this file contains the response time for message sent.
- `results.csv`: this file contains the average response time per application category.

To save the results, you can extract them from the container and save them on your local machine by running the following command:
`$ sudo docker cp mn.subscribers:/planemqxSubscribers/results/ <path_to_directory_on_local_machine>`
where `<path_to_directory_on_local_machine>` is the path to the directory where you wish to copy the results on your local machine.

### Visualizing Results
For visualizing the results, you can run the scripts found in [experiments/results](experiments/results).

## Defining Custom System Configurations

### Defining the IoT System Specifications
We describe IoT environments to be simulated using `JSON` files that include information about: 
 - deployed IoT devices: their id (`deviceId`), name (`deviceName`), number of messages sent per second (`publishFrequency`), size of the messages produced by the device in Bytes (`messageSize`), a probability distribution according to which the devices generate messages (`distribution`), and the topic(s) to which the device publishes messages to (`publishesTo`)
 - deployed applications: their id (`applicationId`), name (`applicationName`), category (`applicationCategory`), priority, rate at which the application processes incoming messages (`processingRate`), distribution according to which the applications process messages (`processingDistribution`), and the topic(s) that the application subscribes to (`subscribesTo`). 
 - application categories: their id (`categoryId`) and name (`categoryName`).
 - topics: their id (`topicId`), name (`topicName`), client that publish to the topic (`publishers`), and clients that subscribe to the topic (`subscribers`).

The `data` directory in each project (i.e., `planemqx-agent/data`, `publishers/data`, `subscribers/data`) contains sample IoT system specifications that have been used for experiments presented in [1], as well as other sample system specifications. These can readily be used without any modification. However, you can still define your own system specifications by following the same structure of the provided files.

### Setting Configurations
#### Setting the simulation scenario
To select the system specifications to be used in the simulation, you can do so through the `config/config.properties` that you can find in the `planemqx-agent`, `publishers`, and `subscribers` directories. In particular, you need to modify the `scenario` variable in the `config.properties` file to set it to the path of the `JSON` file containing the scenario that you would like to simulate. You have to do so for `planemqx-agent/config/config.properties`, `publishers/config/config.properties`, and `subscribers/config/config.properties`.
#### Using the AI planner plan
PlanEMQX relies on AI planning for providing optimal data flow configuration settings. For this purpose, the `data` directory includes an example output of running the AI planner. 
In the `config.properties`, you can include the path to the plan through the `configuration` variable.
For more details about generating plans using AI planning, please refer to the [PlanIoT guide](https://github.com/satrai-lab/planiot).
#### Setting the experiment duration
You can set the experiment duration (in milliseconds) with the `duration` variable in the `config.properties` file. We advise you to set a longer duration for the subscribers than the publishers. This is to ensure that all messages produced by publishers are received by subscribers.
#### Setting the bandwidth between PlanEMQX and applications
To set the value for the bandwidth for the network link between PlanEMQX and applications, you can do so in the `experiments/experimental_framework.py` script (line 79).
You can then [run your experiment](#running-experiments-with-planemqx).


[1] H. Hajj Hassan, G. Bouloukakis, L. Scalzotto, N. Khaled, D. Conan, A. Kattepur, D. Belaïd.  A Message Broker Architecture for Adaptive Data Exchange in the IoT. 21st IEEE International Conference on Software Architecture - Companion (ICSA-C 2024).