package ru.curs.windows;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;
import ru.curs.counting.model.Fraud;
import ru.curs.counting.model.Outcome;
import ru.curs.counting.model.Score;
import ru.curs.windows.configuration.KafkaConfiguration;
import ru.curs.windows.configuration.TopologyConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.EVENT_SCORE_TOPIC;
import static ru.curs.counting.model.TopicNames.FRAUD_TOPIC;

public class TestTopology {

    private TestInputTopic<String, Bet> betsTopic;
    private TestInputTopic<String, EventScore> scoreTopic;
    private TestOutputTopic<String, Fraud> fraudTopic;
    private TopologyTestDriver topologyTestDriver;
    @BeforeEach
    public void setUp() {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration().createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(topology, config.asProperties());
        betsTopic = topologyTestDriver.createInputTopic(BET_TOPIC, Serdes.String().serializer(),
                new JsonSerde<>(Bet.class).serializer());
        scoreTopic = topologyTestDriver.createInputTopic(EVENT_SCORE_TOPIC, Serdes.String().serializer(),
                new JsonSerde<>(EventScore.class).serializer());
        fraudTopic = topologyTestDriver.createOutputTopic(FRAUD_TOPIC, Serdes.String().deserializer(),
                new JsonSerde<>(Fraud.class).deserializer());
    }

    @AfterEach
    void tearDown() {
        topologyTestDriver.close();
    }

    void putBet(Bet value) {
        betsTopic.pipeInput(value.key(), value);
    }

    void putScore(EventScore value) {
        scoreTopic.pipeInput(value.getEvent(), value);
    }


    @Test
    public void nearBetsFound() {
        long current = System.currentTimeMillis();     //1:0
        putScore(new EventScore("Turkey-Moldova", new Score().goalHome(), current));
        putBet(new Bet("alice", "Turkey-Moldova", Outcome.A, 1, 1.5, current - 100));
        putBet(new Bet("bob", "Turkey-Moldova", Outcome.H, 1, 1.5, current - 100)); //<<<<
        putBet(new Bet("bob", "Turkey-Moldova", Outcome.H, 1, 1.5, current + 100));
        putBet(new Bet("bob", "Turkey-Moldova", Outcome.H, 1, 1.5, current - 5000));

        Fraud expectedFraud = Fraud.builder()
                .bettor("bob")
                .match("Turkey-Moldova")
                .outcome(Outcome.H)
                .amount(1)
                .odds(1.5)
                .lag(100)
                .build();


        List<Fraud> frauds = fraudTopic.readValuesToList();
        assertEquals(1, frauds.size());
        assertEquals(expectedFraud, frauds.get(0));
    }


}
