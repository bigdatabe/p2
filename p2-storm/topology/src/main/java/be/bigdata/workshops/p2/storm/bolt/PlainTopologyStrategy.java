package be.bigdata.workshops.p2.storm.bolt;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import be.bigdata.workshops.p2.storm.bolt.SentimentBolt;
import be.bigdata.workshops.p2.storm.topology.strategy.TopologyStrategy;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 4:48 PM
 */
public class PlainTopologyStrategy implements TopologyStrategy {
    @Override
    public StormTopology build(IRichSpout twitterSpout) {
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("tweets-collector", twitterSpout, 1);
        // builder.setBolt("hashtag-sumarizer", new TwitterSumarizeHashtags()).shuffleGrouping("tweets-collector");
        builder.setBolt("sentiment-analyzer", new SentimentBolt()).shuffleGrouping("tweets-collector");
        return builder.createTopology();
    }
}
