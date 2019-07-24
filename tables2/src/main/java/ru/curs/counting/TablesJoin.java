package ru.curs.counting;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class TablesJoin {

	public static void main(String[] args) {
		new SpringApplicationBuilder(TablesJoin.class).headless(false).run(args);
	}

}

