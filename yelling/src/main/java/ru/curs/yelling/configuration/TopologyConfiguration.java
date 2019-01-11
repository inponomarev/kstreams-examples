package ru.curs.yelling.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopologyConfiguration {
    @Bean
    Topology createTopology(StreamsBuilder streamsBuilder) {
        streamsBuilder.
                stream("foo",
                        Consumed.with(Serdes.String(), Serdes.String())
                ).mapValues((ValueMapper<String, String>) String::toUpperCase)
                .to("bar", Produced.with(Serdes.String(), Serdes.String()));
        return streamsBuilder.build();
    }
}
