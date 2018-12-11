package com.faisal.grpc.server

import io.grpc.ServerBuilder

fun main(args: Array<String>) {

    val eventServer = ServerBuilder.forPort(50052)
            .addService(EventsServiceImpl())
            .build()
    eventServer.start()
    println("Event Server 2 is Running now!")

    Runtime.getRuntime().addShutdownHook( Thread{
        eventServer.shutdown()
    } )


    eventServer.awaitTermination()
}