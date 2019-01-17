package ru.curs.counting.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Tweet {
    private final String user;
    private final String body;
}
