package ru.curs.counting.configuration;

import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Service;
import ru.curs.counting.model.GeoLocation;

import java.util.Arrays;

@Service
public class GeoLocationProducer {
    private final DataFactory df = new DataFactory();

    public GeoLocation nextLocation() {
        String city = df.getCity();
        String user = df.getItem(Arrays.asList("jbaruch", "yegor256"));
        return new GeoLocation(user, city);
    }
}
