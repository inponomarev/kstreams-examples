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
import ru.curs.counting.data.Total;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;
import ru.curs.counting.model.Outcome;
import ru.curs.counting.model.Score;

import java.util.function.Function;
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


        KTable<String, Long> totalValues = input.groupByKey().aggregate(
                () -> 0L, (k, v, a) -> a + Math.round(v.getAmount() * v.getOdds()),
                Materialized.with(Serdes.String(), new JsonSerde<>(Long.class))
        );
                /*  Key: "Germany-Belgium:H"
                    Value: 171340L <<total value
                */


        KTable<String, Total> totals =
                totalValues.mapValues((k, v)->new Total(k.substring(0, k.lastIndexOf(':')), v),
                Materialized.with(Serdes.String(), new JsonSerde<>(Total.class)));

        /*  Key: "Germany-Belgium:H"
            Value: {match: "Germany-Belgium",
                    total: 171340L }
        */


        KTable<String, EventScore> eventScores = streamsBuilder.table(EVENT_SCORE_TOPIC,
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
        KTable<String, String> joined = totals.join(eventScores,
                // lambda t: return t.getMatch()
                Total::getMatch,
                (total, score) -> {
                    return String.format("(%s)\t%d", score.getScore(), total.getTotal());
                }
        );


/*
        KTable<String, String> joined = totals.join(eventScores,
                (String betkey) ->  betkey.substring(0, betkey.lastIndexOf(':') ),
                (Long total, EventScore eventScore) -> String.format("(%s)\t%d", eventScore.getScore(), total));
 */
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
