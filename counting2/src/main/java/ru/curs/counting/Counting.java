package ru.curs.counting;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class Counting {

	public static void main(String[] args) {
		new SpringApplicationBuilder(Counting.class).headless(false).run(args);
	}

}

