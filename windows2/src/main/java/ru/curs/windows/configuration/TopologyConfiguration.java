package ru.curs.windows.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;
import ru.curs.counting.model.Fraud;
import ru.curs.counting.model.TopicNames;
import ru.curs.windows.transformer.ScoreTransformer;

import java.time.Duration;

import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.EVENT_SCORE_TOPIC;

@Configuration
@RequiredArgsConstructor
public class TopologyConfiguration {

    @Bean
    public Topology createTopology(StreamsBuilder streamsBuilder) {

        KStream<String, Bet> bets = streamsBuilder.
                stream(BET_TOPIC,
                        Consumed.with(
                                Serdes.String(), new JsonSerde<>(Bet.class))
                                .withTimestampExtractor((record, previousTimestamp) ->
                                        ((Bet) record.value()).getTimestamp()
                                )
                );
        /*Key: "Germany-Belgium:H"
         Value: Bet{
                   bettor    = John Doe;
                   match     = Germany-Belgium;
                   outcome   = H;
                   amount    = 100;
                   odds      = 1.7;
                   timestamp = 1554215083873;
                }
        */

        KStream<String, EventScore> eventScores = streamsBuilder.stream(EVENT_SCORE_TOPIC,
                Consumed.with(
                        Serdes.String(), new JsonSerde<>(EventScore.class))
                        .withTimestampExtractor((record, previousTimestamp) ->
                                ((EventScore) record.value()).getTimestamp()));
        /*
            Key: Germany-Belgium
            Value: EventScore {
                     event     = Germany-Belgium;
                     score     = 1:1; <<<was 0:1
                     timestamp = 554215083998;
             }
         */

        KStream<String, Bet> winningBets = new ScoreTransformer().transformStream(streamsBuilder, eventScores);
        /*
            Key: Germany-Belgium:H
            Value: Bet {
                     .....
             }
        */

        KStream<String, Fraud> join = bets.join(winningBets,
                (bet, winningBet) ->
                        Fraud.builder()
                                .bettor(bet.getBettor())
                                .outcome(bet.getOutcome())
                                .amount(bet.getAmount())
                                .match(bet.getMatch())
                                .odds(bet.getOdds())
                                .lag(winningBet.getTimestamp() - bet.getTimestamp())
                                .build(),
                JoinWindows.of(Duration.ofSeconds(1)).before(Duration.ZERO),
                StreamJoined.with(Serdes.String(),
                        new JsonSerde<>(Bet.class),
                        new JsonSerde<>(Bet.class)
                ));


        join.peek((k, v) -> System.out.printf(String.format("%s-%s%n", k, v)))
                .to(TopicNames.FRAUD_TOPIC, Produced.with(
                        Serdes.String(),
                        new JsonSerde<>(Fraud.class)
                ));

        Topology topology = streamsBuilder.build();
        System.out.println("==========================");
        System.out.println(topology.describe());
        System.out.println("==========================");
        return topology;
    }

}
