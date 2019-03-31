package ru.curs.counting.configuration;

import lombok.RequiredArgsConstructor;
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class EventSupplier implements Supplier<String> {

    private final DataFactory df;

    @Override
    public String get() {
        //return df.getItem(Arrays.asList("Static vs Dynamic", "Maven vs Gradle", "SQL vs NoSQL", "OOP vs FP"));
        return df.getItem(Arrays.asList("Cyprus-Belgium", "Netherlands-Germany", "Poland-Latvia", "Turkey-Moldova"));
    }
}
