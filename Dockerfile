FROM openkbs/ubuntu-bionic-jdk-mvn-py3

ENV GRAPHVIZ_DOT /usr/local/bin/graphviz/dot

RUN apt-get update

RUN apt-get install -y graphviz asciidoctor

COPY . /data
