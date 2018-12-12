package com.faisal.grpc.client

import com.google.protobuf.Empty
import com.proto.event.AllEventsResponse
import com.proto.event.Event
import com.proto.event.EventsServiceGrpc
import io.grpc.ManagedChannelBuilder

fun main(args: Array<String>) {
    var eventsChannel = ManagedChannelBuilder.forAddress("192.168.0.144", 8080)
            .usePlaintext()
            .build()

    var eventServiceStub = EventsServiceGrpc.newBlockingStub(eventsChannel)

    for(i in 1..20) {
        eventServiceStub.createEvent(Event.newBuilder().setEventId(i).setEventName("Event $i").build())
    }

//    eventServiceStub.allEvents(Empty.getDefaultInstance()).forEachRemaining {
//        t: AllEventsResponse? ->
//        println("The event response if is $t")
//    }

    eventsChannel.shutdown()
}