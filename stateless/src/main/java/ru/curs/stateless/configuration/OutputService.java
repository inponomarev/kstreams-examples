package ru.curs.stateless.configuration;

import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class OutputService implements Consumer<String> {
    @Override
    public void accept(String s) {
        System.out.println(s);
    }
}
