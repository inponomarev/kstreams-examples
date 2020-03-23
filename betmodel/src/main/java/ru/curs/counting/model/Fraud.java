package ru.curs.counting.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class Fraud {
    private final String bettor;
    private final String match;
    private final Outcome outcome;
    private final long amount;
    private final double odds;
    private final long lag;
}
