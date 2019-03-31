package ru.curs.windows.configuration;

import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class OutputWriter implements Consumer<String> {

    @Override
    public void accept(String s) {
        System.out.println(s);
    }
}
