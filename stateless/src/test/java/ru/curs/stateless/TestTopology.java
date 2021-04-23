package ru.curs.stateless;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.Outcome;
import ru.curs.stateless.configuration.KafkaConfiguration;
import ru.curs.stateless.configuration.TopologyConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.GAIN_TOPIC;

public class TestTopology {

    private List<String> output = new ArrayList<>();
    private TestInputTopic<String, Bet> inputTopic;
    private TestOutputTopic<String, Long> outputTopic;

    @BeforeEach
    public void setUp() {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration(output::add).createTopology(sb);
        TopologyTestDriver topologyTestDriver = new TopologyTestDriver(topology, config.asProperties());
        inputTopic =
                topologyTestDriver.createInputTopic(BET_TOPIC, Serdes.String().serializer(),
                        new JsonSerde<>(Bet.class).serializer());
        outputTopic =
                topologyTestDriver.createOutputTopic(GAIN_TOPIC, Serdes.String().deserializer(),
                        new JsonSerde<>(Long.class).deserializer());
    }

    @Test
    void testTopology() {
       //arrange
        Bet bet = Bet.builder()
                .bettor("John Doe")
                .match("Germany-Belgium")
                .outcome(Outcome.H)
                .amount(100)
                .odds(1.7).build();

        //act
        inputTopic.pipeInput(bet.key(), bet);
        //assert
        TestRecord<String, Long> record = outputTopic.readRecord();
        assertEquals(bet.key(), record.key());
        assertEquals(170L, record.value().longValue());
    }
}
