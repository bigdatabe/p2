package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class Topology {

    public static void main(String[] args) {
        System.out.println("Team 3");
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("nonsenseBolt", new TestWordSpout());
        builder.setBolt("systemOutBolt", new SystemOutBolt()).shuffleGrouping("nonsenseBolt");
        StormTopology topology = builder.createTopology();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("team3Tolopogy", new Config(), topology);
        Utils.sleep(20);
        cluster.killTopology("team3Tolopogy");
        cluster.shutdown();
    }

}
