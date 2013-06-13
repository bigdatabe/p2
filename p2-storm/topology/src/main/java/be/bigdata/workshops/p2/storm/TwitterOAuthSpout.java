package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TwitterOAuthSpout extends BaseRichSpout {
    /**
     *
     */
    private static final long serialVersionUID = -3267948359533815988L;
    private final String accessToken;
    private final String accessTokenSecret;
    private final String consumerKey;
    private final String consumerSecret;

    SpoutOutputCollector _collector;
    LinkedBlockingQueue<Status> queue = null;
    TwitterStream _twitterStream;

    public TwitterOAuthSpout(String accessToken, String accessTokenSecret, String consumerKey, String consumerSecret) {
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        queue = new LinkedBlockingQueue<Status>(1000);
        _collector = collector;
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                queue.offer(status);
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
        b.setOAuthAccessToken(accessToken);
        b.setOAuthAccessTokenSecret(accessTokenSecret);
        b.setOAuthConsumerKey(consumerKey);
        b.setOAuthConsumerSecret(consumerSecret);

        TwitterStreamFactory fact = new TwitterStreamFactory(
                b.build());

        _twitterStream = fact.getInstance();
        _twitterStream.addListener(listener);
        _twitterStream.sample();
    }

    @Override
    public void nextTuple() {
        Status ret = queue.poll();
        if (ret == null) {
            Utils.sleep(50);
        } else {
            _collector.emit(new Values(ret));
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
        declarer.declare(new Fields("tweet"));
    }
}
