package ru.curs.counting.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Service;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.Outcome;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BetSupplier implements Supplier<Bet> {
    private final DataFactory df;

    private final EventSupplier eventSupplier;

    @Override
    public Bet get() {
        return new Bet(
                df.getFirstName() + " " + df.getLastName(),
                eventSupplier.get(),
                df.getItem(Outcome.values()),
                df.getNumberUpTo(1000),
                System.currentTimeMillis()
        );
    }
}
