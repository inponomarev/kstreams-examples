package ru.curs.counting.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventScore {
    private final String event;
    private final Score score;
    private final long timestamp;
}
