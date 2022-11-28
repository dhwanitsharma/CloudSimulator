#  Dhwanit Sharma - HW3 CS441
## University of Illinois at Chicago

## Introduction
In this project I have implemented several experiments in CloudPlus simulator to compare their metrics
by implementing various algorithms.

What is CloudSim?

Cloud computing is the leading approach for delivering reliable, secure, fault-tolerant, sustainable, and scalable computational services. Hence timely, repeatable, and controllable methodologies for performance evaluation of new cloud applications and policies before their actual development are required.

## Project Structure
The project structure is as follows:
1. Src
    1. main
        1. resources --- Contains the useful resources and config file
            1. application.conf
            2. logback.xml
        2. scala --- Contains all the task files and helper files.
            1. HelperUtils
                1. CreateLogger.scala
                2. VmAllocationPolicyRandom
            2. Simulations
                1. Experiment1.scala
                2. Experiment2.scala
                3. Experiment3.scala
                4. Iaas.scala
                5. Paas.scala
                6. Saas.scala
            3. Simulation.scala
    2. test --- Contains all the test files.
        1. scala
            1. TestApplicationConf.scala
            2. TestPattern.scala

## Installation Instructions

### Docker Image 
The docker image for running this project can be found at [DockerHub](https://hub.docker.com/repository/docker/dsharm37/cloudsimulator)
```
docker pull dsharm37/cloudsimulator:firstapp
```

1. Use the following URL to clone the project : 
```
git@github.com:dhwanitsharma/CloudSimulator.git
```

2. In the root directory, run the command "sbt assembly" and this will create a jar in the following path:target/scala-3.1.3
3. Run the jar file directly, without any arguments it will run all the simulations together.
4. To run a particular simulation, follow the following map:
```
1 => Experiment1
2 => Experiment2
3 => Experiment3
4 => Saas
5 => Paas
6 => Iaas
```

## Installation Instructions - Intellij
1. Use the following URL to clone the project :
```
git@github.com:dhwanitsharma/CloudSimulator.git
```
2. Select File-> Project from existing sources... and select the folder
3. Select Import project from external model and select sbt.
4. Select scala version as Scala3 and a java version above java 17.
5. Build the program
6. Run the main program from Simulation.scala. 
7. To run all the simulations, do not pass any argument, else modify the run config 
and add the argument as per the Map mentioned above to run a particular simulation

## Experiment Description
### Experiment 1 : Comparing performance of Simple, Round Robin, Best Fit, First Fit and Custom  vmAllocation policy 
In this experiment, all the simulations are run on the same configurations of Datacenter, hosts, VM and cloudlets.
I have also implemented a custom random vmAllocation policy for this experiment.

The result of the experiment can be found here : **[Experiment1 - Report](report/Experiment1.md)**


### Experiment 2 : Compare different VM scheduling policies(Space shared, Time Shared)
In this experiment, all the simulations are run on the same configurations of Datacenter, hosts, VM and cloudlets.

I have simulated the following scenarios:
1. We have two hosts with SpaceShared Scheduling
2. We have two hosts with TimeShared Scheduling
3. We have one host SpaceShared and one host Timeshared

The result of the experiment can be found here : **[Experiment2 - Report](report/Experiment2.md)**

### Experiment 3 : Implemented a network topology for different datacenters
In this experiment, I have simulated brite topology

The result of the experiment can be found here : **[Experiment3 - Report](report/Experiment3.md)**

### Experiment 4 : Saas Simulator
Created a simulation which provides user an option to select the following setting to run a Saas system.
1. Slow: Ram : 1024 Storage : 1024 BandWidth : 1000 Pes : 2 Mips : 1000
2. Medium: Ram : 2048 Storage : 2048 BandWidth : 2000 Pes : 4 Mips : 2000
3. Fast Ram : 4096 Storage : 4096 BandWidth : 4000 Pes : 8 Mips : 4000

The result of the experiment can be found here : **[Saas - Report](report/Saas.md)**

### Experiment 5 : Paas Simulator

Created a simulation which provides user an option to select the following setting to run a Paas system. Each system has 2 vms, this can changed from application.conf
1. Slow (2 vms): Ram : 1024 Storage : 1024 BandWidth : 1000 Pes : 2 Mips : 1000
2. Medium (2 vms): Ram : 2048 Storage : 2048 BandWidth : 2000 Pes : 4 Mips : 2000
3. Fast (2 vms) Ram : 2048 Storage : 2048 BandWidth : 4000 Pes : 8 Mips : 4000

The result of the experiment can be found here : **[Paas - Report](report/Paas.md)**

### Experiment 6 : Iaas Simulator

Created a simulation which provides user an option to select the following setting to run a Paas system. 
Each system has 2 vms, this can changed from application.conf 
1. Slow: 1. 4 vms of Ram : 1024 Storage : 1024 BandWidth : 1000 Pes : 2 Mips : 1000 
   1. 2 vms of Ram : 2048 Storage : 2048 BandWidth : 2000 Pes : 4 Mips : 2000 
2. Medium: 1. 1 vms of Ram : 2048 Storage : 2048 BandWidth : 2000 Pes : 4 Mips : 2000 
   1. 1 vm of Ram : 2048 Storage : 2048 BandWidth : 4000 Pes : 8 Mips : 4000 
   2. 1 vm of Ram : 1024 Storage : 1024 BandWidth : 1000 Pes : 2 Mips : 1000 
3. Fast: 1. 2 vms of Ram : 2048 Storage : 2048 BandWidth : 4000 Pes : 8 Mips : 4000

The result of the experiment can be found here : **[Iaas - Report](report/Iaas.md)**


## Output
The output will be as follows:
1. If the pattern is found in the log files, the console of client will print out the following:

`Log messages are found with hash <MD5 HASHCODE> `

2. If the pattern is not found in the log files, the console of client will print out the following:

`No log statements found for given parameters`

## AWS Deployment
As shown in the video, build the file using ```sbt clean compile assembly``` to build the jar. The jar is then uploaded to the Lambda function. Use the instruction in the video to set
and test the Lambda function. Video for the project <a href="https://youtu.be/hTdQP7JUfa0" target="_blank">Video</a> 