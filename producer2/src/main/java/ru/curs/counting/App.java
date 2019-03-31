package ru.curs.counting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import ru.curs.counting.configuration.BetSupplier;
import ru.curs.counting.configuration.EventScoreSupplier;
import ru.curs.counting.model.Bet;
import ru.curs.counting.model.EventScore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.curs.counting.model.TopicNames.BET_TOPIC;
import static ru.curs.counting.model.TopicNames.EVENT_SCORE_TOPIC;


@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args).close();
    }

    @Autowired
    private KafkaTemplate<String, Object> template;


    @Autowired
    private BetSupplier betSupplier;

    @Autowired
    private EventScoreSupplier eventScoreSupplier;

    @SneakyThrows
    private void writeBets() {
        while (true) {
            Bet bet = betSupplier.get();
            System.out.println(bet.toString());
            template.send(BET_TOPIC, bet.key(), bet);
            Thread.sleep(50);
        }
    }

    @SneakyThrows
    private void writeScores() {
        while (true) {
            EventScore es = eventScoreSupplier.get();
            System.out.println(es.toString());
            template.send(EVENT_SCORE_TOPIC, es.getEvent(), es);
            Thread.sleep(4000);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(this::writeBets);
        executorService.submit(this::writeScores);
    }

}

