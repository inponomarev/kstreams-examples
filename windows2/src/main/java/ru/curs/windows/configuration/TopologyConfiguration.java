package ru.curs.windows.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;
import ru.curs.windows.transformer.ScoreTransformer;

import java.time.Duration;
import java.util.function.Consumer;

import static ru.curs.counting.model.Outcome.H;
import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.EVENT_SCORE_TOPIC;

@Configuration
@RequiredArgsConstructor
public class TopologyConfiguration {

    private final Consumer<String> output;

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

        KStream<String, String> join = bets.join(winningBets,
                (bet, winningBet) ->
                        String.format("%s bet %s=%s %d",
                                bet.getBettor(),
                                bet.getOutcome(),
                                winningBet.getOutcome(),
                                winningBet.getTimestamp() - bet.getTimestamp()),
                JoinWindows.of(Duration.ofSeconds(1)).before(Duration.ZERO),
                Joined.with(Serdes.String(),
                        new JsonSerde<>(Bet.class),
                        new JsonSerde<>(Bet.class)
                ));

        join.foreach((k, v) -> output.accept(String.format("%s-%s", k, v)));

        Topology topology = streamsBuilder.build();
        System.out.println("==========================");
        System.out.println(topology.describe());
        System.out.println("==========================");
        return topology;
    }

}
