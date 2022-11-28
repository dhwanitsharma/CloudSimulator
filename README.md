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
### Task 1 : Upload the LogGenerator into a EC2 instance and save the logs in a S3 bucket.
The LogGenerator generates logs based on the config file. I have updated the LogGenerator File to make a new log file every 5 minutes. To achieve this
I have used a class file which extends the RollingFileAppender class. Example of the file name of the log generated is ```LogFileGenerator-20221028-19-20``` where
20221028 is the date in YYYYMMDD format, and 19-20 is the time, in HH-MM format.

After the logs are generated, I have created a S3Uploader class which will upload the log directory in the S3 bucket. For the purpose of this project all the logs are generated for a single day.

### Task 2 : Create a Lambda function to detect a certain pattern in the log messages
The Lambda function has two major parts:
1. First it iterates through the file name which on the basis of the timestamp. It calculates the starting time and ending time bases on the
   time and the interval provided in the input. Using this time frame, function iterates through the folder and reads the file name of all the log files. It compares the HH-MM part of the file name with the interval
   and makes a ```list of key``` of the logs in the bucket. If the input time range logs are not available, function returns a string ```false```.
2. If the list is populated then we iterate through the file list, the timeStamps are taken as an array. Using the binary search, we find the closest value to the start and end time in the array. Using this we get
   the index of the messages which are in desired time interval. Using this index we slice the original log file and iterate through those logs
   to find the ```pattern``` in the log messages. Example:
    1. In the list, we have a log file from index 0 to 10000. The starting time stamp is 19:20:00 and ending time stamp 19:25:00. The target value is 19:22 with interval of 00:01. Now the starting interval is 19:21:00 and ending is 19:23:00.
    2. We find the starting closest index of 19:21 as 2500 and closest index 19:23 as 7500. Now, we use these indexes 2500 and 7500 to slice the original log file, and return the MD5 hash of the log message which is matched to the input ``pattern``

### Task 3 : Create a gRPC Server
Created a gRPC server, which calls the Lambda function. The gRPC Server uses the protobuf to define the structure for the data. The server uses ``Rest POST`` call to the Lambda function. The example of the JSON used by the server.
```
{
    "interval" : "00:02:00",
    "time" : "2022-10-28-19-22-00-000",
    "pattern" : "Rsxg"
}
```

### Task 4 : Create a gRPC Client
Created a gRPC client, which calls the gRPC server. The gRPC client also uses the protobuf to define the structure for the data. The client uses ```S3.conf``` configuration file for input where the inputs are defined ```grpc``` section of the file.
The client uses the following as inputs:
```
time = "2022-10-28-19-22-00:000"
detect_patter = "Rsxg"
interval = "00:02:00"
```

## Output
The output will be as follows:
1. If the pattern is found in the log files, the console of client will print out the following:

`Log messages are found with hash <MD5 HASHCODE> `

2. If the pattern is not found in the log files, the console of client will print out the following:

`No log statements found for given parameters`

## AWS Deployment
As shown in the video, build the file using ```sbt clean compile assembly``` to build the jar. The jar is then uploaded to the Lambda function. Use the instruction in the video to set
and test the Lambda function. Video for the project <a href="https://youtu.be/hTdQP7JUfa0" target="_blank">Video</a> 