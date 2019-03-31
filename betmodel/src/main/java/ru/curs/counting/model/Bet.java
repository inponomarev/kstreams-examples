package ru.curs.counting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Bet {
    final String bettor;
    final String match;
    final Outcome outcome;
    final long amount;
    final long timestamp;

    @JsonCreator
    public Bet(
            @JsonProperty("bettor") String bettor,
            @JsonProperty("match") String match,
            @JsonProperty("outome") Outcome outcome,
            @JsonProperty("amount") long amount,
            @JsonProperty("timestamp") long timestamp
    ) {
        this.bettor = bettor;
        this.match = match;
        this.outcome = outcome;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String key() {
        return String.format("%s:%s", match, outcome);
    }
}
