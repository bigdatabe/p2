package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * To run this topology you should execute this main as: 
 * java -cp XXX.jar twitter.streaming.Topology <track> <twitterUser> <twitterPassword>
 *
 * @see https://github.com/storm-book/examples-ch04-spouts/blob/master/src/main/java/twitter/streaming/Topology.java
 * for the original version.
 *
 */
public class Topology {
 
	public static void main(String[] args) throws InterruptedException {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("tweets-collector", new ApiStreamingSpout(),1);
		builder.setBolt("hashtag-sumarizer", new TwitterSumarizeHashtags()).
			shuffleGrouping("tweets-collector"); 

		LocalCluster cluster = new LocalCluster();
		Config conf = new Config();
		conf.put("track", args[0]);
		conf.put("user", args[1]);
		conf.put("password", args[2]);

		cluster.submitTopology("twitter-test", conf, builder.createTopology());
	}
}