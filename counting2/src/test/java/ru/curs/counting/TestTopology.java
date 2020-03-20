package ru.curs.counting;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
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
    private TestInputTopic<String, Bet> inputTopic;

    @BeforeEach
    public void setUp() throws IOException {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration(
                new GUI(VirtualScreenBuilder.screen())).createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(
                topology, config.asProperties());
        inputTopic = topologyTestDriver.createInputTopic(BET_TOPIC, Serdes.String().serializer(),
                new JsonSerde<>(Bet.class).serializer());
    }

    void placeBet(Bet value) {
        inputTopic.pipeInput(value.key(), value);
    }

    @Test
    void testTopology() {
        placeBet(new Bet("foo", "A-B", Outcome.A, 20, 1.1, 0));
        placeBet(new Bet("foo", "A-B", Outcome.A, 30, 1.1,0));
        placeBet(new Bet("foo", "A-B", Outcome.H, 10, 1.1,0));

        KeyValueStore<String, Long> store = topologyTestDriver.getKeyValueStore(TotallingTransformer.STORE_NAME);
        assertEquals(55, store.get("A-B:A").intValue());
        assertEquals(11, store.get("A-B:H").intValue());

    }
}
