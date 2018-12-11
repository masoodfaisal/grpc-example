package com.faisal.grpc.server


import com.google.protobuf.Empty
import com.proto.event.AllEventsResponse
import com.proto.event.CreateEventResponse
import com.proto.event.Event
import com.proto.event.EventsServiceGrpc.EventsServiceImplBase
import io.grpc.stub.StreamObserver


class EventsServiceImpl : EventsServiceImplBase() {

    override fun createEvent(request: Event?, responseObserver: StreamObserver<CreateEventResponse>?) {
        println("Event Created ")
        responseObserver?.onNext(CreateEventResponse.newBuilder().setSuccess("true").build())
        responseObserver?.onCompleted()
    }

    override fun allEvents(request: Empty?, responseObserver: StreamObserver<AllEventsResponse>?) {

        for (i in 1..10) {
            var event = Event.newBuilder().setEventId(i).setEventName("Event $i").build()
            responseObserver?.onNext(AllEventsResponse.newBuilder().setEvent(event).build())
            println("Serving Events.....")
            Thread.sleep(1000)
        }

        responseObserver?.onCompleted()

    }


}