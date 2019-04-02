package ru.curs.stateless;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class StatelessApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatelessApplication.class, args);
	}

}

