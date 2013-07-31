package be.bigdata.workshops.p2.storm.topology.strategy;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import be.bigdata.workshops.p2.storm.bolt.DebugBolt;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 4:48 PM
 */
public class YahooFinanceTopologyStrategy implements TopologyStrategy {
    @Override
    public StormTopology build(IRichSpout yahooFinanceSpout) {
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("price-collector", yahooFinanceSpout, 1);
        // builder.setBolt("hashtag-sumarizer", new TwitterSumarizeHashtags()).shuffleGrouping("tweets-collector");
        builder.setBolt("daychange-analyzer", new DebugBolt()).shuffleGrouping("price-collector");
        return builder.createTopology();
    }
}
