package com.faisal.eds

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import java.io.IOException
import java.net.InetSocketAddress
import com.sun.net.httpserver.HttpServer


// THIS CLASS IS FOR DEMO PURPOSES ONLY
// DO NOT USE IN PRODUCTION


//https://www.envoyproxy.io/docs/envoy/latest/configuration/overview/v2_overview#config-overview-v2-management-server
fun main(args: Array<String>) {
    val server = HttpServer.create(InetSocketAddress(7070), 0)
//    server.createContext("/v2/discovery:endpoints", EDSHandler())
    server.createContext("/", EDSHandler())
    server.executor = null // creates a default executor
    server.start()
}

internal class EDSHandler : HttpHandler {
    @Throws(IOException::class)
    override fun handle(t: HttpExchange) {

        t.sendResponseHeaders(200, EDS_RESPONSE.length.toLong())
        t.responseHeaders.add("content-type", "application/json")
        val os = t.responseBody
        os.write(EDS_RESPONSE.toByteArray())
        os.close()
    }
}

val EDS_RESPONSE = "{\n" +
        "\t\"version_info\": \"0\",\n" +
        "\t\"resources\": [\n" +
        "\t\t{\n" +
        "\t\t\t\"@type\": \"type.googleapis.com/envoy.api.v2.ClusterLoadAssignment\",\n" +
        "\t\t\t\"cluster_name\": \"grpc_service\",\n" +
        "\t\t\t\"endpoints\": [\n" +
        "\t\t\t\t{\n" +
        "\t\t\t\t\t\"lb_endpoints\": [\n" +

        "\t\t\t\t\t\t{\n" +
        "\t\t\t\t\t\t\t\"endpoint\": {\n" +
        "\t\t\t\t\t\t\t\t\"address\": {\n" +
        "\t\t\t\t\t\t\t\t\t\"socket_address\": {\n" +
        "\t\t\t\t\t\t\t\t\t\t\"address\": \"192.168.0.144\",\n" +
        "\t\t\t\t\t\t\t\t\t\t\"port_value\": 50051\n" +
        "\t\t\t\t\t\t\t\t\t}\n" +
        "\t\t\t\t\t\t\t\t}\n" +
        "\t\t\t\t\t\t\t}\n" +
        "\t\t\t\t\t\t}\n" +

        "\t\t\t\t\t\t,{\n" +
        "\t\t\t\t\t\t\t\"endpoint\": {\n" +
        "\t\t\t\t\t\t\t\t\"address\": {\n" +
        "\t\t\t\t\t\t\t\t\t\"socket_address\": {\n" +
        "\t\t\t\t\t\t\t\t\t\t\"address\": \"192.168.0.144\",\n" +
        "\t\t\t\t\t\t\t\t\t\t\"port_value\": 50052\n" +
        "\t\t\t\t\t\t\t\t\t}\n" +
        "\t\t\t\t\t\t\t\t}\n" +
        "\t\t\t\t\t\t\t}\n" +
        "\t\t\t\t\t\t}\n" +


        "\t\t\t\t\t]\n" +
        "\t\t\t\t}\n" +
        "\t\t\t]\n" +
        "\t\t}\n" +
        "\t]\n" +
        "}"
//https://www.envoyproxy.io/docs/envoy/latest/configuration/overview/v2_overview
//val EDS_RESPONSE = "version_info: \"0\"\n" +
//        "resources: [{\n" +
//        "               \"@type\": \"type.googleapis.com/envoy.api.v2.ClusterLoadAssignment\",\n" +
//        "               \"cluster_name\": \"grpc_service\",\n" +
//        "               \"endpoints\":\n" +
//        "  - lb_endpoints:\n" +
//        "    - endpoint:\n" +
//        "       address:\n" +
//        "         socket_address:\n" +
//        "           address: 127.0.0.1\n" +
//        "           port_value: 50051\n"