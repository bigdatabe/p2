package be.bigdata.workshops.p2.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;

/**
 * To run this topology you should execute this main as: 
 * java -cp XXX.jar be.bigdata.workshops.p2.storm.Topology <track> <twitterUser> <twitterPassword>
 *
 * @see https://github.com/storm-book/examples-ch04-spouts/blob/master/src/main/java/twitter/streaming/Topology.java
 * for the original version.
 *
 */
public class Topology {
 
	public static void main(String[] args) throws InterruptedException {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("tweets-collector", new ApiStreamingSpout(),1);
//		builder.setBolt("hashtag-sumarizer", new TwitterSumarizeHashtags()).shuffleGrouping("tweets-collector"); 
        builder.setBolt("sentiment-analyzer", new SentimentBolt()).shuffleGrouping("tweets-collector"); 

		LocalCluster cluster = new LocalCluster();
		Config conf = new Config();
		conf.put("track", "#FAKE10factsaboutme");
		conf.put("user", "bebigdatabetwit");
		conf.put("password", "donderdag10");
        conf.put("sentiment_file", "sentiment_scores.txt");

		cluster.submitTopology("twitter-test", conf, builder.createTopology());
	}
}