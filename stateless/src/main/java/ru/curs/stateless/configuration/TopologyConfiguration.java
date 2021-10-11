package ru.curs.stateless.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;

import java.util.function.Consumer;

import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.GAIN_TOPIC;

@Configuration
@RequiredArgsConstructor
public class TopologyConfiguration {

    private final Consumer<String> out;

    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {
        KStream<String, Bet> input = streamsBuilder.
                stream(BET_TOPIC,
                        Consumed.with(Serdes.String(),
                                new JsonSerde<>(Bet.class))
                );
        /*Key: "Germany-Belgium:H"
         Value: Bet{
                   bettor = John Doe;
                   match = Germany-Belgium;
                   outcome = H;
                   amount = 100;
                   odds = 1.7;
                   timestamp = 1554215083998;
                }
        */
        KStream<String, Long> gain
                = input.mapValues(v -> {
            long val = Math.round(v.getAmount() * v.getOdds());
            return val;
        });
        /*  Key: "Germany-Belgium:H"
            Value: 170L
        */

        gain.to(GAIN_TOPIC, Produced.with(Serdes.String(),
                new JsonSerde<>(Long.class)));
        Topology topology = streamsBuilder.build();

        System.out.println("===============================");
        System.out.println(topology.describe());
        System.out.println("===============================");

        // https://zz85.github.io/kafka-streams-viz/

        return topology;
    }
}
