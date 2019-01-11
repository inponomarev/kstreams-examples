package ru.curs.yelling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class YellingApplication {

	public static void main(String[] args) {
		SpringApplication.run(YellingApplication.class, args);
	}

}

