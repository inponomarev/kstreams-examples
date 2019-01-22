package ru.curs.counting.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Bean("TWEET_TOPIC_NAME")
    String tweetTopicName() {
        return "in";
    }

    @Bean("GEO_TOPIC_NAME")
    String geoTopicName() {
        return "geo";
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }


    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic tweetTopic(@Qualifier("TWEET_TOPIC_NAME") String name) {
        return new NewTopic(name, 10, (short) 1);
    }

    @Bean
    public NewTopic locationTopic(@Qualifier("GEO_TOPIC_NAME") String name) {
        Map<String, String> props = new HashMap<>();
        props.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
        return new NewTopic(name, 10, (short) 1).configs(props);
    }


    @Bean
    public NewTopic locationTopic5() {
        Map<String, String> props = new HashMap<>();
        props.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
        return new NewTopic("geo5", 5, (short) 1).configs(props);
    }
}
