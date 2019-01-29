package ru.curs.counting.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.curs.counting.cli.GUI;
import ru.curs.counting.transformer.CountingTransformer;

@Configuration
@RequiredArgsConstructor
public class TopologyConfiguration {
    private final GUI gui;

    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {
        KStream<String, String> filtered = streamsBuilder.
                stream("in",
                        Consumed.with(Serdes.String(), Serdes.String())
                ).filter((k, v) -> v.toLowerCase().contains("jpoint"));

        KStream<String, Integer> counted =
                new CountingTransformer()
                        .transformStream(streamsBuilder, filtered);
        counted.foreach((k, v) -> {
            gui.update(k, v);
        });

        return streamsBuilder.build();
    }
}
