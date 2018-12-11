# gRPC Server side load balancing with Envoy proxy
[gRPC](https://grpc.io) is an open source high performance RPC framework that runs anywhere.

In this blog, I am showcasing how [envoy proxy](https://www.envoyproxy.io) can be used to provide server side load balancing.
This is not a tutorial for [gRPC](https://grpc.io)  or [envoy proxy](https://www.envoyproxy.io), if you want to learn these technologies, google is your friend. 
You can start with [learn envoy](https://www.learnenvoy.io), [gRPC](https://grpc.io/docs/tutorials/) and [protocl buffers](https://developers.google.com/protocol-buffers/docs/proto3).


## Setup
* First we need to define a [protobuf message](https://developers.google.com/protocol-buffers/docs/overview) which will serves as the contract between the client and the server. Please refer to [event.proto](https://github.com/masoodfaisal/grpc-example/blob/master/src/main/proto/events/events.proto)
```proto
syntax  = "proto3";

import "google/protobuf/empty.proto";

package event;

option java_package = "com.proto.event";
option java_multiple_files = true;


message Event {
    int32 event_id = 1;
    string event_name = 2;
    repeated string event_hosts = 3;
}

enum EVENT_TYPE {
    UNDECLARED = 0;
    BIRTHDAY = 1;
    MARRIAGE = 2;
}

message CreateEventResponse{
    string success = 1;
}

message AllEventsResponse{
    Event event = 1;
}

service EventsService{
    rpc CreateEvent(Event) returns (CreateEventResponse) {};
    rpc AllEvents(google.protobuf.Empty) returns (stream AllEventsResponse) {};
}
```
* This message will then be used by the [gradle gRPC](https://grpc.io/blog/kotlin-gradle-projects) plugin to generate stubs. These stubs will be used by client and the server. You can run gradle's generateProto task to generate the stubs.
* Now it is time to write the [server](https://github.com/masoodfaisal/grpc-example/blob/master/src/main/java/com/faisal/grpc/server/EventServer.kt)
```kotlin
val eventServer = ServerBuilder.forPort(50051)
            .addService(EventsServiceImpl()) //refer to the server implementation
            .build()
    eventServer.start()
    println("Event Server is Running now!")

    Runtime.getRuntime().addShutdownHook( Thread{
        eventServer.shutdown()
    } )


    eventServer.awaitTermination()
```
* Once the boiler plate code of the server is finished [see previous step], we write the server [business logic](https://github.com/masoodfaisal/grpc-example/blob/master/src/main/java/com/faisal/grpc/server/EventsServiceImpl.kt).
```kotlin
    override fun createEvent(request: Event?, responseObserver: StreamObserver<CreateEventResponse>?) {
        println("Event Created ")
        responseObserver?.onNext(CreateEventResponse.newBuilder().setSuccess("true").build())
        responseObserver?.onCompleted()
    }

```
* Let's write a [client](https://github.com/masoodfaisal/grpc-example/blob/master/src/main/java/com/faisal/grpc/client/EventClient.kt) to consume our events service.
```kotlin
fun main(args: Array<String>) {
    var eventsChannel = ManagedChannelBuilder.forAddress("10.0.0.112", 8080)
            .usePlaintext()
            .build()

    var eventServiceStub = EventsServiceGrpc.newBlockingStub(eventsChannel)

    eventServiceStub.createEvent(Event.newBuilder().setEventId(1).setEventName("Event 001").build())

    eventsChannel.shutdown()
}
```
* I have copied the server code into another [file](https://github.com/masoodfaisal/grpc-example/blob/master/src/main/java/com/faisal/grpc/server/EventServer2.kt) and change the port number to mimic multiple instances of our events service.
* Envoy proxy configuration has three parts. All these settings are in [envoy yaml](https://github.com/masoodfaisal/grpc-example/blob/master/envoy-docker/envoy.yaml)
  * A frontend service. This service will receive request from the clients.
  * A backend service. The frontend service will loadbalance the calls to this set of servers.
  * An optional EDS endpoint. This is another service which will provide the list of backend endpoints. This way envoy can dynamically adjust to the available servers. I have written this EDS service as a [simple class](https://github.com/masoodfaisal/grpc-example/blob/master/src/main/java/com/faisal/eds/EDSServer.kt).


# Execution

* Copy the project locally.
```bash
git clone https://github.com/masoodfaisal/grpc-example.git
```
* Build the project using gradle
```bash
./gradlew generateProto
./gradlew build
```

* Run the EDS Server
```bash
./gradlew -PmainClass=com.faisal.eds.EDSServerKt execute

```

* Run the first and second instance of the server
```bash
./gradlew -PmainClass=com.faisal.grpc.server.EventServerKt execute
./gradlew -PmainClass=com.faisal.grpc.server.EventServer2Kt execute
```

* Run enovy proxy
```bash
cd envoy-docker
docker build -t envoy:grpclb .
docker run -p 9090:9090 -p 8080:8080 envoy:grpclb 
```

* Run client, multiple times and you can see the calls are being distributed in a round robin
```bash
./gradlew -PmainClass=com.faisal.grpc.client.EventClientKt execute
```



