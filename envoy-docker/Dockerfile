#FROM envoyproxy/envoy:latest

FROM fedora:latest

RUN dnf install -y 'dnf-command(copr)' && dnf copr enable -y vbatts/envoy && dnf install -y envoy curl net-tools telnet

COPY envoy.yaml /etc/envoy/envoy.yaml

CMD ["/usr/bin/envoy", "-c", "/etc/envoy/envoy.yaml", "-l", "trace", "--service-cluster", "cluster0", "--service-node", "node0"]


#https://github.com/vbatts/envoy-examples/tree/master/front-proxy
#docker build -t envoy:v1 .
#docker run -d --name envoy -p 9090:9090 -p 8080:8080 envoy:v1
#  clusters:
#    - name: grpc_service
#      connect_timeout: 0.25s
#      lb_policy: ROUND_ROBIN
#      type: EDS
#      eds_cluster_config:
#        eds_config:
#          api_config_source:
#            api_type: GRPC
#            grpc_services:
#              envoy_grpc:
#                cluster_name: xds_cluster
#    - name: xds_cluster
#      connect_timeout: 0.25s
#      type: STATIC
#      lb_policy: ROUND_ROBIN
#      http2_protocol_options: {}
#      load_assignment:
#        cluster_name: xds_cluster
#        endpoints:
#          - lb_endpoints:
#              - endpoint:
#                  address:
#                    socket_address:
#                      address: 127.0.0.1
#                      port_value: 7070