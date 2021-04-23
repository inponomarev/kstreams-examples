package ru.curs.counting.data;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.curs.counting.model.Outcome;

@Data
@Builder
@RequiredArgsConstructor
public class Total {
    private final String match;
    private final long total;
}
