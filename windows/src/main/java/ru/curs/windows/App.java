package ru.curs.windows;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class App {

	public static void main(String[] args) {
		new SpringApplicationBuilder(App.class).headless(false).run(args);
	}

}

