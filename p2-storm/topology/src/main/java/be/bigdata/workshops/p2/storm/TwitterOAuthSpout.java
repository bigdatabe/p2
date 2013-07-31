package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.json.DataObjectFactory;

public class TwitterOAuthSpout extends BaseRichSpout {
    /**
     *
     */
    private static final long serialVersionUID = -3267948359533815988L;
    private static final JSONParser jsonParser = new JSONParser();
    SpoutOutputCollector _collector;
    LinkedBlockingQueue<Object> queue = null;
    TwitterStream _twitterStream;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        queue = new LinkedBlockingQueue<Object>(1000);
        _collector = collector;
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                // Dirty, but this is what we need to be compatible with ApiStreamingSpout
                String rawJson = DataObjectFactory.getRawJSON(status);
                Object json;
                try {
                    json = jsonParser.parse(rawJson);
                    queue.offer(json);
                } catch (ParseException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice sdn) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onStallWarning(StallWarning arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onException(Exception e) {
            }
        };

        ConfigurationBuilder b = new ConfigurationBuilder();
        final String accessToken = (String) conf.get("accessToken");
        final String accessTokenSecret = (String) conf.get("accessTokenSecret");
        final String consumerKey = (String) conf.get("consumerKey");
        final String consumerSecret = (String) conf.get("consumerSecret");

        // We need this to get to the JSON status
        b.setJSONStoreEnabled(true);
        b.setOAuthAccessToken(accessToken);
        b.setOAuthAccessTokenSecret(accessTokenSecret);
        b.setOAuthConsumerKey(consumerKey);
        b.setOAuthConsumerSecret(consumerSecret);

        TwitterStreamFactory fact = new TwitterStreamFactory(
                b.build());

        // See http://twitter4j.org/oldjavadocs/3.0.3/twitter4j/FilterQuery.html
        FilterQuery filterQuery = new FilterQuery();
        String [] keywords = {"$AAPL", "$GOOG", "$CA", "$INFA", "$IBM", "$ORCL", "$HPQ", "$MSFT", "$YHOO", "$CSCO", "$AMD", "$INTL"};
        filterQuery.track(keywords);

        _twitterStream = fact.getInstance();
        _twitterStream.addListener(listener);
        _twitterStream.sample();

        // Use the filter function to get filtered results (instead of sample)
        // _twitterStream.filter(filterQuery);
    }

    @Override
    public void nextTuple() {
        Object ret = queue.poll();
        if (ret == null) {
            Utils.sleep(50);
        } else {
            _collector.emit(new Values("*", ret));
        }
    }

    @Override
    public void close() {
        _twitterStream.shutdown();
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config ret = new Config();
        ret.setMaxTaskParallelism(1);
        return ret;
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("track", "tweet"));
    }
}
