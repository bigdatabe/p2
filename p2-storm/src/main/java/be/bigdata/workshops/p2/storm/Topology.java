package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class Topology {

    public static void main(final String[] args) {
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("words", new TwitterSpout("BigDatabeTeam2","donderdag10"), 10);
        builder.setBolt("debug", new DebugBolt(), 3);
        //builder.setBolt("exclaim1", new ExclamationBolt(), 3).shuffleGrouping("words");
        //builder.setBolt("exclaim2", new ExclamationBolt(), 2).shuffleGrouping("exclaim1");

        final Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(2);

        final LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, builder.createTopology());
        Utils.sleep(10000);

        cluster.killTopology("test");
        cluster.shutdown();
    }
}
