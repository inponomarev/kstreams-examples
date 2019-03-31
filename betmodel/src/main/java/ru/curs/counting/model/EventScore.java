package ru.curs.counting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventScore {
    private final String event;
    private final Score score;
    private final long timestamp;

    @JsonCreator
    public EventScore(@JsonProperty("event") String event,
                      @JsonProperty("score") Score score,
                      @JsonProperty("timestamp") long timestamp) {
        this.event = event;
        this.score = score;
        this.timestamp = timestamp;
    }
}
