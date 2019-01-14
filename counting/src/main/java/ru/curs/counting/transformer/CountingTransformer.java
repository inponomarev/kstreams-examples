package ru.curs.counting.transformer;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueStore;
import ru.curs.counting.util.StatefulTransformer;

import java.util.Optional;

public class CountingTransformer implements StatefulTransformer<
        String, Integer,
        String, String,
        String, Integer> {
    public static final String STORE_NAME = "jpoint-counted";
    private final Serde<String> keySerde = Serdes.String();
    private final Serde<Integer> valueSerde = Serdes.Integer();

    @Override
    public String storeName() {
        return STORE_NAME;
    }

    @Override
    public Serde<String> storeKeySerde() {
        return keySerde;
    }

    @Override
    public Serde<Integer> storeValueSerde() {
        return valueSerde;
    }

    @Override
    public KeyValue<String, Integer> transform(String key, String value, KeyValueStore<String, Integer> stateStore) {
        int current = Optional.ofNullable(stateStore.get(key)).orElse(0) + 1;
        stateStore.put(key, current);
        return KeyValue.pair(key, current);
    }
}
