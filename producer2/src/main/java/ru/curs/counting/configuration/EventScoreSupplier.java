package ru.curs.counting.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.curs.counting.model.EventScore;
import ru.curs.counting.model.Score;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class EventScoreSupplier implements Supplier<EventScore> {
    private final EventSupplier es;
    private final Map<String, Score> scoreMap = new HashMap<>();

    @Override
    public EventScore get() {
        String event = es.get();
        Score score = scoreMap.compute(event, (k, v) -> {
            if (v == null)
                return new Score();
            else {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    return v.goalHome();
                } else {
                    return v.goalAway();
                }
            }
        });
        return new EventScore(event, score, System.currentTimeMillis());
    }

}
