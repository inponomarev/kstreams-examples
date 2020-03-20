package ru.curs.stateless.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfiguration {
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration getStreamsConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stateless-demo-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        // props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
        //         LogAndContinueExceptionHandler.class);
        KafkaStreamsConfiguration streamsConfig = new KafkaStreamsConfiguration(props);
        return streamsConfig;
    }

    @Bean
    StreamsBuilderFactoryBeanConfigurer uncaughtExceptionConfigurer(
            @Qualifier(KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_BUILDER_BEAN_NAME) StreamsBuilderFactoryBean factoryBean,
            ApplicationContext ctx) {
        return new StreamsBuilderFactoryBeanConfigurer(factoryBean, ctx);
    }

    @AllArgsConstructor
    static class StreamsBuilderFactoryBeanConfigurer implements InitializingBean {
        private final StreamsBuilderFactoryBean factoryBean;
        private final ApplicationContext ctx;

        @Override
        public void afterPropertiesSet() {

            this.factoryBean.setUncaughtExceptionHandler(
                    (t, e) -> {
                        log.error("Uncaught exception in thread {}", t.getName(), e);
                        factoryBean.getKafkaStreams().close(Duration.ofSeconds(10));
                        log.info("Kafka streams closed.");
                    });
            this.factoryBean.setStateListener((newState, oldState) -> {
                if (newState == KafkaStreams.State.NOT_RUNNING) {
                    log.info("Now exiting the application.");
                    SpringApplication.exit(ctx, () -> 1);
                }
            });
        }
    }

}
