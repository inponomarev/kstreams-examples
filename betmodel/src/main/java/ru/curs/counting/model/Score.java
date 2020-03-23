package ru.curs.counting.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Score {
    private final int home;
    private final int away;

    public Score() {
        this(0, 0);
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
