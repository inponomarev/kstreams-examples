package ru.curs.yelling;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import ru.curs.yelling.configuration.KafkaConfiguration;
import ru.curs.yelling.configuration.TopologyConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTopology {

    TopologyTestDriver topologyTestDriver;

    @BeforeEach
    public void setUp() {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration().createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(
                topology, config.asProperties());

        topologyTestDriver = new TopologyTestDriver(
                topology, config.asProperties());
    }

    void processValue(String key, String value) {
        ConsumerRecordFactory<String, String> factory = new ConsumerRecordFactory<>(Serdes.String().serializer(),
                Serdes.String().serializer());
        topologyTestDriver.pipeInput(factory.create("foo", key, value));
    }

    @Test
    void testTopology() {
        processValue("a", "hello, world");
        ProducerRecord<String, String> bar = topologyTestDriver.readOutput("bar", Serdes.String().deserializer(), Serdes.String().deserializer());
        assertEquals("a", bar.key());
        assertEquals("HELLO, WORLD", bar.value());
    }
}
