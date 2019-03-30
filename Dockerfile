FROM openkbs/ubuntu-bionic-jdk-mvn-py3

RUN apt-get update && apt-get install -y graphviz
