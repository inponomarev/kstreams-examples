package ru.curs.windows.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;
import ru.curs.windows.transformer.ScoreTransformer;

import java.time.Duration;
import java.util.function.Consumer;

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

        KStream<String, EventScore> eventScores = streamsBuilder.stream(EVENT_SCORE_TOPIC,
                Consumed.with(
                        Serdes.String(), new JsonSerde<>(EventScore.class))
                        .withTimestampExtractor((record, previousTimestamp) ->
                                ((EventScore) record.value()).getTimestamp()));

        KStream<String, Bet> outcomes = new ScoreTransformer().transformStream(streamsBuilder, eventScores);

        KStream<String, String> join = bets.join(outcomes,
                (bet, sureBet) -> String.format("%s bet %s=%s %d", bet.getBettor(), bet.getOutcome(), sureBet.getOutcome(),
                        sureBet.getTimestamp() - bet.getTimestamp()),
                JoinWindows.of(Duration.ofSeconds(1)).before(Duration.ZERO),
                Joined.with(Serdes.String(),
                        new JsonSerde<>(Bet.class),
                        new JsonSerde<>(Bet.class)
                ));

        join.foreach((k, v) -> output.accept(String.format("%s-%s", k, v)));

        return streamsBuilder.build();
    }

}
