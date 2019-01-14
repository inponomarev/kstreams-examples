package ru.curs.counting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

}

