package ru.curs.windows;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;
import ru.curs.counting.model.Outcome;
import ru.curs.counting.model.Score;
import ru.curs.windows.configuration.KafkaConfiguration;
import ru.curs.windows.configuration.TopologyConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.EVENT_SCORE_TOPIC;

public class TestTopology {
    private TopologyTestDriver topologyTestDriver;
    private ConsumerRecordFactory<String, Bet> betFactory = new ConsumerRecordFactory<>(Serdes.String().serializer(),
            new JsonSerde<>(Bet.class).serializer());
    private List<String> output = new ArrayList<>();

    private ConsumerRecordFactory<String, EventScore> scoreFactory = new ConsumerRecordFactory<>(Serdes.String().serializer(),
            new JsonSerde<>(EventScore.class).serializer());

    @BeforeEach
    public void setUp() throws IOException {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration(output::add).createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(
                topology, config.asProperties());
        output.clear();
    }

    void putBet(Bet value) {
        topologyTestDriver.pipeInput(betFactory.create(BET_TOPIC, value.key(), value));
    }

    void putScore(EventScore value) {
        topologyTestDriver.pipeInput(scoreFactory.create(EVENT_SCORE_TOPIC, value.getEvent(), value));
    }


    @Test
    public void nearBetsFound() {
        long current = System.currentTimeMillis();
        putScore(new EventScore("Turkey-Moldova", new Score().goalHome(), current));
        putBet(new Bet("alice", "Turkey-Moldova", Outcome.A, 1, 1.5, current - 100));
        putBet(new Bet("bob", "Turkey-Moldova", Outcome.H, 1, 1.5, current - 100));
        putBet(new Bet("bob", "Turkey-Moldova", Outcome.H, 1, 1.5, current - 5000));
        assertEquals(Arrays.asList("Turkey-Moldova:H-bob bet H=H 100"), output);
    }


}
