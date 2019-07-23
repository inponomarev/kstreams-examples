package ru.curs.stateless;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.Outcome;
import ru.curs.stateless.configuration.KafkaConfiguration;
import ru.curs.stateless.configuration.TopologyConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.GAIN_TOPIC;

public class TestTopology {

    TopologyTestDriver topologyTestDriver;
    private List<String> output = new ArrayList<>();
    private ConsumerRecordFactory<String, Bet> betFactory = new ConsumerRecordFactory<>(Serdes.String().serializer(),
            new JsonSerde<>(Bet.class).serializer());

    @BeforeEach
    public void setUp() {
        KafkaStreamsConfiguration config = new KafkaConfiguration().getStreamsConfig();
        StreamsBuilder sb = new StreamsBuilder();
        Topology topology = new TopologyConfiguration(output::add).createTopology(sb);
        topologyTestDriver = new TopologyTestDriver(
                topology, config.asProperties());
    }

    @Test
    void testTopology() {
        Bet bet = Bet.builder()
                .bettor("John Doe")
                .match("Germany-Belgium")
                .outcome(Outcome.H)
                .amount(100)
                .odds(1.7).build();

        topologyTestDriver.pipeInput(betFactory.create(BET_TOPIC, bet.key(), bet));

        ProducerRecord<String, Long> record = topologyTestDriver.readOutput(GAIN_TOPIC, new StringDeserializer(),
                new JsonDeserializer<>(Long.class));

        assertEquals(bet.key(), record.key());
        assertEquals(170L, record.value().longValue());

    }
}
