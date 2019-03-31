package ru.curs.counting.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Score {
    private final int home;
    private final int away;

    public Score() {
        this(0, 0);
    }

    @JsonCreator
    private Score(
            @JsonProperty("home")
            int home,
            @JsonProperty("away")
            int away) {
        this.home = home;
        this.away = away;
    }

    public Score goalHome() {
        return new Score(home + 1, away);
    }

    public Score goalAway() {
        return new Score(home, away + 1);
    }

    public String toString(){
        return String.format("%d:%d", home, away);
    }
}
