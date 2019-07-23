package ru.curs.counting.util;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

@SuppressWarnings("Duplicates")
public interface StatefulTransformer<SK, SV, IK, IV, RK, RV> {
    String storeName();

    Serde<SK> storeKeySerde();

    Serde<SV> storeValueSerde();

    KeyValue<RK, RV> transform(IK key, IV value, KeyValueStore<SK, SV> stateStore);

    default boolean isPersistent() {
        return false;
    }

    default void init(ProcessorContext context) {
        //do nothing by default, can be overridden
    }

    default void close() {
        //do nothing by default, can be overridden
    }

    default KStream<RK, RV> transformStream(StreamsBuilder builder, KStream<IK, IV> input) {
        String storeName = storeName();
        class KTransformer implements Transformer<IK, IV, KeyValue<RK, RV>> {
            private KeyValueStore<SK, SV> stateStore;

            @Override
            @SuppressWarnings("unchecked")
            public void init(ProcessorContext context) {
                stateStore = (KeyValueStore<SK, SV>) context.getStateStore(storeName);
                StatefulTransformer.this.init(context);
            }

            @Override
            public KeyValue<RK, RV> transform(IK key, IV value) {
                return StatefulTransformer.this.transform(key, value, stateStore);
            }

            @Override
            public void close() {
            }
        }
        KeyValueBytesStoreSupplier storeSupplier =
                isPersistent() ?
                        Stores.persistentKeyValueStore(storeName) :
                        Stores.inMemoryKeyValueStore(storeName);
        StoreBuilder<KeyValueStore<SK, SV>> storeBuilder =
                Stores.keyValueStoreBuilder(storeSupplier,
                        storeKeySerde(),
                        storeValueSerde());
        builder.addStateStore(storeBuilder);
        return input.transform(() -> new KTransformer(), storeName);
    }
}
