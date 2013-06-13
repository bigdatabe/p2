package be.bigdata.workshops.p2.storm;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/**
 * To run this topology you should execute this main as: java -cp XXX.jar be.bigdata.workshops.p2.storm.Topology <track>
 * <twitterUser> <twitterPassword>
 * 
 * @see https://github.com/storm-book/examples-ch04-spouts/blob/master/src/main/java/twitter/streaming/Topology.java for the
 *      original version.
 */
public class Topology {

    public static void main(final String[] args) throws InterruptedException {
        final ArgumentParser parser =
            ArgumentParsers.newArgumentParser("stocks").defaultHelp(true).description("realtime twitter stock monitor.");
        parser.addArgument("-a", "--accessToken");
        parser.addArgument("-s", "--accessTokenSecret");
        parser.addArgument("-c", "--consumerKey");
        parser.addArgument("-e", "--consumerSecret");

        try {
            final Namespace namespace = parser.parseArgs(args);
            final String accessToken = namespace.getString("accessToken");
            final String accessTokenSecret = namespace.getString("accessTokenSecret");
            final String consumerKey = namespace.getString("consumerKey");
            final String consumerSecret = namespace.getString("consumerSecret");

            // We can switch between realtime/stubbed spout here.
            final IRichSpout twitterSpout =
                createRealtimeTwitterSpout(accessToken, accessTokenSecret, consumerKey, consumerSecret);
            final StormTopology topology = createTopology(twitterSpout);
            executeTopology(topology);
        } catch (final ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (final Exception ex) {
            throw new RuntimeException("error while running topology", ex);
        }
    }

    /* package */static IRichSpout createRealtimeTwitterSpout(final String accessToken, final String accessTokenSecret,
        final String consumerKey, final String consumerSecret) {
        return new TwitterOAuthSpout(accessToken, accessTokenSecret, consumerKey, consumerSecret);
    }

    /**
     * Run the given topology.
     * 
     * @param topology to run.
     */
    /* package */static void executeTopology(final StormTopology topology) {
        final LocalCluster cluster = new LocalCluster();
        final Config conf = new Config();
        // conf.put("track", args[0]);
        // conf.put("user", args[1]);
        // conf.put("password", args[2]);

        conf.put("track", "#FAKE10factsaboutme"); // Dummy keyword that is currently trending
        conf.put("user", "bebigdatabetwit");
        conf.put("password", "donderdag10");

        conf.put("sentiment_file", "sentiment_scores.txt");

        cluster.submitTopology("twitter-test", conf, topology);

        // Sleep XX seconds, then kill this clusters - closing the connections etc...
        Utils.sleep(30000);
        cluster.killTopology("twitter-test");
        cluster.shutdown();
    }

    /**
     * Create the actual {@link StormTopology}.
     * 
     * @param twitterSpout a {@link IRichSpout} compatible with the twitter api output interface (can be realtime or stub
     *            instance).
     * @return the {@link StormTopology}.
     */
    /* package */static StormTopology createTopology(final IRichSpout twitterSpout) {
        final TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("tweets-collector", twitterSpout, 1);
        // builder.setBolt("hashtag-sumarizer", new TwitterSumarizeHashtags()).shuffleGrouping("tweets-collector");
        builder.setBolt("sentiment-analyzer", new SentimentBolt()).shuffleGrouping("tweets-collector");
        return builder.createTopology();
    }
}