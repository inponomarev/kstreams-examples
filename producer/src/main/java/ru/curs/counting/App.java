package ru.curs.counting;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import ru.curs.counting.configuration.TweetProducer;
import ru.curs.counting.model.Tweet;


@SpringBootApplication
public class App implements CommandLineRunner {

	public static Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		SpringApplication.run(App.class, args).close();
	}

	@Autowired
	private KafkaTemplate<String, String> template;

	@Autowired
	private TweetProducer tweetProducer;

	@Autowired
	@Qualifier("TOPIC_NAME")
	private String topicName;

	@Override
	public void run(String... args) throws Exception {
		while (true)
		{
			Tweet nextTweet = tweetProducer.nextTweet();
			this.template.send(topicName, nextTweet.getUser(), nextTweet.getBody());
			logger.info(nextTweet.toString());
			Thread.sleep(50);
		}

	//	logger.info("All sent");
	}

}

