package ru.curs.counting.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static ru.curs.counting.model.TopicNames.*;

@Configuration
public class KafkaConfiguration {

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }


    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic betTopic() {
        return new NewTopic(BET_TOPIC, 10, (short) 1);
    }

    @Bean
    public NewTopic gainTopic() {
        return new NewTopic(GAIN_TOPIC, 10, (short) 1);
    }

    @Bean
    public NewTopic fraudTopic() {
        return new NewTopic(FRAUD_TOPIC, 10, (short) 1);
    }

    @Bean
    public NewTopic scoreTopic() {
        Map<String, String> props = new HashMap<>();
        props.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
        return new NewTopic(EVENT_SCORE_TOPIC, 10, (short) 1).configs(props);
    }
}
