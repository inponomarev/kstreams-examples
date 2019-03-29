FROM openkbs/ubuntu-bionic-jdk-mvn-py3

RUN apt-get update

RUN apt-get install -y graphviz

COPY . /data
