# AnonymousChat

Project fot the exam of **Architetture Distribuite per il Cloud**

Student: Squitieri Lucio 05220
MD5:

# Index

* [Introduction](#Introduction)
* [Problem Statement](#Problem-Statement)
* [Basic Features](#Basic Features)
* [Additional Features](#Additional-Features)
* [Deployment](#Deployment)
    * [Used technologies](#Used-technologies)
    * [Project structure](#Project-structure)
* [Testing](#Testing)
* [How to run](#How-to-run)


## Introduction

The project constis in the creation of a p2p network that function as an anonymous chat.

## Problem Statement

Design and develop an anonymous chat API based on P2P Network. Each peer can send messages on a public chat room in an anonymous way. 
The system allows the users to create a new room, join in a room, leave a room, and send messages. As described in the
[AnonymousChat Java API](https://github.com/spagnuolocarmine/distributedsystems-unisa/blob/master/homework/AnonymousChat.java)

## Basic features
The basic functions of the project were:

1. Create a room;
2. Join in a room;
3. Leave a room;
4. Send messages to a specific room;

## Additional Features

Features added to the previous ones, that include:

1. Read all the messages sent in a specific room;
2. See the joined rooms for the single user;
3. See how many users are present in a specific room;
4. Leave all the rooms previously joined;

## Deployment

### Used technologies

- IntelliJ IDEA
- Apache Maven
- Tom P2P
- Java
- JUnit
- Docker

### Project structure

    - src/main/java/
	    - beans/
              # Message.java: class that represents a Message of the users
              # Room.java: class that reperesents a Room
	    - interfaces/
              # AnonymousChat.java: interface with the sign of the methods for the implementation of this project
              # MessageListener.java: interface with the sign of the method to parse the received messages
        - impls/
              # AnonymousChatI.java: class that implements the methods of the interface "AnonymousChat"
	    
        # Main: Main class of this project

    - test/java/
        # Tester.java: testing class

    - Dockerfile: a text document that contains all the commands a user could call on the command line to assemble an image.

## Testing

The functionality test was done through the JUnit library.\

To create the test cases:

- initially it was sufficient to create only the 4 peers that were used in them;
- test cases were created based on the functionality to be tested, comprehensive of the possible exception such as sending a message to a room the user didn't join.

Each test case uses different room names and message contents than any other test case, so that it does not conflict
with them.

*The test class is [this](./src/test/java/TestAnonymousChat.java)*.

## How to run

This application is provided using Docker container, running on a local machine. See the Dockerfile, for the building
details.

First you have to build your docker container:

```bash
docker build --no-cache -t anonchat .

### Start the master peer

After that you can start the master peer using the below command:

```bash
docker run -i  --name master -e MASTERIP="127.0.0.1" -e ID=0 anonchat
```

| Options |                   Description                    |
|:-------:|:------------------------------------------------:|
| --name  |          Assign a name to the container          |
|   -i    |       Keep STDIN open even if not attached       |
|   -e    |            Set environment variables             |

Through this command I start a peer that will act as master.

| Variable |          Description           |
|:--------:|:------------------------------:|
| MASTERIP |   the master peer ip address   |
|    ID    | the **unique** id of your peer |

**⚠️ Remember to run the master peer using the ID=0.**

**_Note:_** After the first launch you can launch the master node using the following command:

```bash
docker start -i master
```

### Start a generic peer

When master is started you have to check the IP Address of your container:

- Check the docker <container ID>: ```docker ps```
- Check the IP address: ```docker inspect <container ID>```

Now you can start your peers varying the unique peer id:

```
docker run -i --name peer-1 -e MASTERIP="172.17.0.2" -e ID=1 anonchat
```
