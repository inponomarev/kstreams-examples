package ru.curs.counting.configuration;

import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DataFactoryConfig {
    @Bean
    @Scope("prototype")
    DataFactory getDataFactory() {
        DataFactory df = new DataFactory();
        df.randomize((int) System.nanoTime());
        return df;
    }
}
