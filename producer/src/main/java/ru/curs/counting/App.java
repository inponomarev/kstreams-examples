package ru.curs.counting;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import ru.curs.counting.configuration.GeoLocationProducer;
import ru.curs.counting.configuration.TweetProducer;
import ru.curs.counting.model.GeoLocation;
import ru.curs.counting.model.Tweet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@SpringBootApplication
public class App implements CommandLineRunner {

	public static Logger logger = LoggerFactory.getLogger(App.class);


	public static void main(String[] args) {
		SpringApplication.run(App.class, args).close();
	}

	@Autowired
	private KafkaTemplate<String, String> template;

	@Autowired
	private GeoLocationProducer locationProducer;

	@Autowired
	private TweetProducer tweetProducer;

	@Autowired
	@Qualifier("TWEET_TOPIC_NAME")
	private String tweetTopicName;

	@Autowired
	@Qualifier("GEO_TOPIC_NAME")
	private String geoTopicName;

	@SneakyThrows
	private void writeTweets() {
		while (true)
		{
			Tweet nextTweet = tweetProducer.nextTweet();
			this.template.send(tweetTopicName, nextTweet.getUser(), nextTweet.getBody());
			logger.info(nextTweet.toString());
			Thread.sleep(50);
		}
	}

	@SneakyThrows
	private void writeLocations() {
		while (true)
		{
			GeoLocation nextLocation = locationProducer.nextLocation();
			this.template.send(geoTopicName, nextLocation.getUser(), nextLocation.getLocation());
			logger.info(nextLocation.toString());
			Thread.sleep(4000);
		}
	}

	@Override
	public void run(String... args) throws Exception {
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.submit(this::writeTweets);
		executorService.submit(this::writeLocations);
	}

}

