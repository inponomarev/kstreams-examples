package ru.curs.counting.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GeoLocation {
    private final String user;
    private final String location;
}
