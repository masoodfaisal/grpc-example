FROM fedora:latest
MAINTAINER Faisal Masood fmasood@redhat.com
RUN dnf install -y 'dnf-command(copr)' && dnf copr enable -y vbatts/envoy && dnf install -y envoy curl net-tools telnet

COPY envoy.yaml /etc/envoy/envoy.yaml

CMD ["/usr/bin/envoy", "-c", "/etc/envoy/envoy.yaml", "-l", "trace", "--service-cluster", "cluster0", "--service-node", "node0"]


