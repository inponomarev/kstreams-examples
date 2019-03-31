package ru.curs.counting;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.cli.GUI;
import ru.curs.counting.configuration.KafkaConfiguration;
import ru.curs.counting.configuration.TopologyConfiguration;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.Outcome;
import ru.curs.counting.transformer.TotallingTransformer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.curs.counting.model.TopicNames.BET_TOPIC;

public class TestTopology {


    private TopologyTestDriver topologyTestDriver;
    private ConsumerRecordFactory<String, Bet> factory = new ConsumerRecordFactory<>(Serdes.String().serializer(),
            new JsonSerde<>(Bet.class).serializer());

    @BeforeEach
    public void setUp() throws IOException {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration(
                new GUI(VirtualScreenBuilder.screen())).createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(
                topology, config.asProperties());
    }

    void processValue(String key, Bet value) {
        topologyTestDriver.pipeInput(factory.create(BET_TOPIC, key, value));
    }

    @Test
    void testTopology() {
        processValue("A-B:A", new Bet("foo", "A-B", Outcome.A, 2, 0));
        processValue("A-B:A", new Bet("foo", "A-B", Outcome.A, 3, 0));
        processValue("A-B:H", new Bet("foo", "A-B", Outcome.H, 1, 0));

        KeyValueStore<String, Long> store = topologyTestDriver.getKeyValueStore(TotallingTransformer.STORE_NAME);
        assertEquals(5, store.get("A-B:A").intValue());
        assertEquals(1, store.get("A-B:H").intValue());

    }
}
