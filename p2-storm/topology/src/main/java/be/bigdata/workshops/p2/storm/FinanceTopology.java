package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import be.bigdata.workshops.p2.storm.bolt.DebugBolt;
import be.bigdata.workshops.p2.storm.bolt.finance.FinanceTrendBolt;
import be.bigdata.workshops.p2.storm.spout.YahooFinanceSpout;


public class FinanceTopology {

	public static String FINANCE_AGGREGATION_PERIOD_PARAM = "finance.window.size";

    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new YahooFinanceSpout(), 1);
        builder.setBolt("financeTrend" , new FinanceTrendBolt()).fieldsGrouping("spout", new Fields("stock"));
        builder.setBolt("debug", new DebugBolt(), 12).shuffleGrouping("financeTrend");
        
        Config conf = new Config();
        conf.setDebug(true);
        conf.put(FINANCE_AGGREGATION_PERIOD_PARAM, 5000l);
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS,  1);


        if(args!=null && args.length > 0) {
            conf.setNumWorkers(3);

            StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
        } else {
            conf.setMaxTaskParallelism(3);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("finance-stream", conf, builder.createTopology());

            Thread.sleep(20000);

            cluster.shutdown();
        }
    }


}