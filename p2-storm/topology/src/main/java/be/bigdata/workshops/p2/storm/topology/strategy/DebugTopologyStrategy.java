package be.bigdata.workshops.p2.storm.topology.strategy;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import be.bigdata.workshops.p2.storm.bolt.DebugBolt;
import be.bigdata.workshops.p2.storm.bolt.SentimentBolt;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 5:19 PM
 */
public class DebugTopologyStrategy implements TopologyStrategy {
    @Override
    public StormTopology build(IRichSpout twitterSpout) {
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("tweets-collector", twitterSpout, 1);
        builder.setBolt("sentiment-analyzer", new SentimentBolt()).shuffleGrouping("tweets-collector");
        builder.setBolt("debug", new DebugBolt()).shuffleGrouping("tweets-collector");
        return builder.createTopology();
    }
}
