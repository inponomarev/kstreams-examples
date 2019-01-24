package ru.curs.windows.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.curs.windows.cli2.GUITextText;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class TopologyConfiguration {
    @Autowired
    private GUITextText gui;

    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm:ss")
                    .withZone(ZoneId.systemDefault());
    private final DateTimeFormatter secondsFormatter =
            DateTimeFormatter.ofPattern("ss")
                    .withZone(ZoneId.systemDefault());

    @Bean
    Topology createTopology(StreamsBuilder streamsBuilder) {


        KStream<String, String> filtered = streamsBuilder.
                stream("in",
                        Consumed.with(Serdes.String(), Serdes.String())
                ).filter((k, v) -> v.toLowerCase().contains("jpoint"));


        TimeWindowedKStream<String, String> windowed = filtered

                .groupByKey().windowedBy(TimeWindows.of(Duration.ofSeconds(5)));


        KTable<Windowed<String>, Long> count = windowed.count();
        count.toStream().map((s, l) -> KeyValue.pair(s.key(), new Windowed<>(l, s.window())))
                .mapValues(s -> formatWindow(s.window()) + " " + s.key().toString())
                .foreach((k, v)->gui.update(k, v));
                //.print(Printed.toSysOut());

        return streamsBuilder.build();
    }

    private String formatWindow(Window w){
        return String.format("%s-%s", timeFormatter.format(w.startTime()),
                secondsFormatter.format(w.endTime()));
    }
}
