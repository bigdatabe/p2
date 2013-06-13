package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class Topology {

    public static void main(String[] args) {
        System.out.println("Team 3");
        StormTopology topology = buildTopology();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("team3Topology", new Config(), topology);
        Utils.sleep(3000);
        cluster.killTopology("team3Topology");
        cluster.shutdown();
    }

    private static StormTopology buildTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("twitterSpout", new TwitterSpout());
        builder.setBolt("sentimentBolt", new SentimentBolt()).shuffleGrouping("twitterSpout");
        return builder.createTopology();
    }

}
