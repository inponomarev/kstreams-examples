package ru.curs.counting.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.cli.GUI;
import ru.curs.counting.model.Bet;
import ru.curs.counting.transformer.TotallingTransformer;

import static ru.curs.counting.model.TopicNames.BET_TOPIC;

@Configuration
@RequiredArgsConstructor
public class TopologyConfiguration {
    private final GUI gui;

    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {
        KStream<String, Bet> input = streamsBuilder.
                stream(BET_TOPIC,
                        Consumed.with(Serdes.String(),
                                new JsonSerde<>(Bet.class))
                );

        KStream<String, Long> counted =
                new TotallingTransformer()
                        .transformStream(streamsBuilder, input);
        counted.foreach((k, v) -> {
            gui.update(k, v);
        });

        return streamsBuilder.build();
    }
}
