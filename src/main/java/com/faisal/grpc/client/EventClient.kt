package com.faisal.grpc.client

import com.google.protobuf.Empty
import com.proto.event.AllEventsResponse
import com.proto.event.Event
import com.proto.event.EventsServiceGrpc
import io.grpc.ManagedChannelBuilder

fun main(args: Array<String>) {
    var eventsChannel = ManagedChannelBuilder.forAddress("10.0.0.112", 8080)
            .usePlaintext()
            .build()

    var eventServiceStub = EventsServiceGrpc.newBlockingStub(eventsChannel)

    eventServiceStub.createEvent(Event.newBuilder().setEventId(1).setEventName("Event 001").build())

//    eventServiceStub.allEvents(Empty.getDefaultInstance()).forEachRemaining {
//        t: AllEventsResponse? ->
//        println("The event response if is $t")
//    }

    eventsChannel.shutdown()
}