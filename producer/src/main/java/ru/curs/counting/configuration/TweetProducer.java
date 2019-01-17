package ru.curs.counting.configuration;

import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.stereotype.Service;
import ru.curs.counting.model.Tweet;

import java.util.Arrays;

@Service
public class TweetProducer {
    private final DataFactory df;

    public TweetProducer() {
        df = new DataFactory();
        df.randomize((int) System.nanoTime());
    }

    private String nextWord() {
        return df.chance(95) ?  df.getRandomWord(): "#jpoint";
    }

    public Tweet nextTweet() {
        StringBuilder body = new StringBuilder();
        int l;
        while ((l = body.length()) == 0 || df.chance(80)) {
            String append = l == 0 ? nextWord() :
                    " " + nextWord();
            if (l > 0 && l + append.length() > 280)
                break;
            body.append(append);
        }

        String user = df.getItem(Arrays.asList("jbaruch", "yegor256", "tolkv"));
        return new Tweet(user, body.toString());
    }
}
