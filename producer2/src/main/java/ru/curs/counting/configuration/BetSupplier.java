package ru.curs.counting.configuration;

import lombok.RequiredArgsConstructor;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Service;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.Outcome;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BetSupplier implements Supplier<Bet> {
    private final DataFactory df;

    private final EventSupplier eventSupplier;

    @Override
    public Bet get() {
        return Bet.builder()
                .bettor(df.getFirstName() + " " + df.getLastName())
                .match(eventSupplier.get())
                .outcome(df.getItem(Outcome.values()))
                .amount(df.getNumberUpTo(1000))
                .odds(1.0 + Math.round(ThreadLocalRandom.current().nextDouble() * 100) / 100.0)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
