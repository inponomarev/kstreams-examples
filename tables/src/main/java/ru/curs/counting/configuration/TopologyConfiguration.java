package ru.curs.counting.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.curs.counting.cli2.GUITextText;

@Configuration
public class TopologyConfiguration {
    @Autowired
    private GUITextText gui;

    @Bean
    Topology createTopology(StreamsBuilder streamsBuilder) {
        KStream<String, String> filtered = streamsBuilder.
                stream("in",
                        Consumed.with(Serdes.String(), Serdes.String())
                ).filter((k, v) -> v.toLowerCase().contains("jpoint"));

        filtered.print(Printed.toSysOut());
//Counter
        KTable<String, Long> count = filtered.groupByKey().count();
        count.toStream().foreach((k, v) -> {
            gui.update(k, String.valueOf(v));
        });
/*
        KTable<String, String> latest = filtered.groupByKey()
                .reduce((v1, v2) -> v2);
*/
        //latest tweet
   /*    latest.toStream().foreach((k, v) -> {
            gui.update(k, v);
        });
*/
/*
       KTable<String, String> geo = streamsBuilder.table("geo", Consumed.with(Serdes.String(), Serdes.String()));
        KTable<String, String> joined = latest.leftJoin(geo, (v1, v2) -> String.format("%s: %s", v2, v1));

        joined.toStream().foreach((k, v) -> {
            gui.update(k, v);
        });
*/
        /*
//Latest tweet - via topic
        filtered.to("filtered", Produced.with(Serdes.String(), Serdes.String()));

        streamsBuilder.table("filtered", Consumed.with(Serdes.String(), Serdes.String()))
                .toStream().foreach((k, v) -> {
            gui.update(k, v);
        });
*/


        return streamsBuilder.build();
    }
}
