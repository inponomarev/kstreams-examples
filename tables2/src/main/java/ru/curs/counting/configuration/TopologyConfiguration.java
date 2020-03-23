package ru.curs.counting.configuration;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.cli2.GUITextText;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;
import ru.curs.counting.model.Outcome;
import ru.curs.counting.model.Score;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.EVENT_SCORE_TOPIC;

@Configuration
public class TopologyConfiguration {
    @Autowired
    private GUITextText gui;

    @Bean
    Topology createTopology(StreamsBuilder streamsBuilder) {
        KStream<String, Bet> input = streamsBuilder.
                stream(BET_TOPIC,
                        Consumed.with(Serdes.String(),
                                new JsonSerde<>(Bet.class))
                );

        /*Key: "Germany-Belgium:H"
         Value: Bet{
                   bettor    = John Doe;
                   match     = Germany-Belgium;
                   outcome   = H;
                   amount    = 100;
                   odds      = 1.7;
                   timestamp = 1554215083998;
                }
        */
        KTable<String, Long> totals = input.groupByKey().aggregate(
                () -> 0L, (k, v, a) -> a + Math.round(v.getAmount() * v.getOdds()),
                Materialized.with(Serdes.String(), new JsonSerde<>(Long.class))
        );

        /*  Key: "Germany-Belgium:H"
            Value: 171340L <<total value
        */
        KStream<String, EventScore> eventScores = streamsBuilder.stream(EVENT_SCORE_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(EventScore.class)
                ));
        /*
            Key: Germany-Belgium
            Value: EventScore {
                     event     = Germany-Belgium;
                     score     = 1:0;
                     timestamp = 554215083998;
             }
         */
        KStream<String, Score> scores = eventScores
                .flatMap((k, v) ->
                        Stream.of(Outcome.H, Outcome.A).map(o ->
                                KeyValue.pair(
                                        String.format("%s:%s", k, o), v))
                                .collect(Collectors.toList()))
                /*
                    Key: Germany-Belgium:H
                    Value: EventScore

                    Key: Germany-Belgium:A
                    Value: EventScore
                 */
                .mapValues(EventScore::getScore);

        KTable<String, Score> tableScores =
                scores.groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Score.class)))
                        .reduce((a, b) -> b);


        KTable<String, String> joined = totals.join(tableScores,
                (total, eventScore) -> String.format("(%s)\t%d", eventScore, total));

        /*Key: Germany-Belgium:H
          Value: (1:0) 171340*/

        joined.toStream().foreach(gui::update);
        Topology topology = streamsBuilder.build();
        System.out.println("========================================");
        System.out.println(topology.describe());
        System.out.println("========================================");
        return topology;
    }
}
