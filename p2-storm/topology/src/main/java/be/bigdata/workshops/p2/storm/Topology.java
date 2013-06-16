package be.bigdata.workshops.p2.storm;

import be.bigdata.workshops.p2.storm.factories.BasicAuthSpoutFactory;
import be.bigdata.workshops.p2.storm.factories.OAuthSpoutFactory;
import be.bigdata.workshops.p2.storm.factories.SpoutFactory;
import be.bigdata.workshops.p2.storm.factories.StubSpoutFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.*;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/**
 * Switch between different 'origin' spouts on the command line. To use real-time data using your Twitter username and
 * password, use these command line arguments: {@code realtime basic -u <username> -p <password> -t <track>}
 * To use the real-time data using OAuth for authentication, use:
 * {@code realtime oauth -a <accessToken> -s <accessTokenSecret> -c <consumerKey> -e <consumerSecret>}
 * To use stub data, use: {@code stub -f <filename>}
 *
 * Note that track filtering only works with basic authentication for now!
 *
 * @see https://github.com/storm-book/examples-ch04-spouts/blob/master/src/main/java/twitter/streaming/Topology.java for the
 *      original version.
 */
public class Topology {
    private static final Logger LOG = Logger.getLogger(Topology.class);

    private static final StubSpoutFactory STUB_SPOUT_FACTORY = new StubSpoutFactory();
    private static final OAuthSpoutFactory OAUTH_SPOUT_FACTORY = new OAuthSpoutFactory();
    private static final BasicAuthSpoutFactory BASIC_AUTH_SPOUT_FACTORY = new BasicAuthSpoutFactory();
    private static final String FACTORY = "factory";

    public static void main(final String[] args) throws InterruptedException {
        final ArgumentParser parser =
            ArgumentParsers.newArgumentParser("stocks").defaultHelp(true).description("real-time twitter stock monitor.");
        final Subparser realtimeParser = parser.addSubparsers().addParser("realtime");

        final Subparser stubParser = parser.addSubparsers().addParser("stub").setDefault(FACTORY, STUB_SPOUT_FACTORY);
        final Subparser oauthParser = realtimeParser.addSubparsers().addParser("oauth").setDefault(FACTORY, OAUTH_SPOUT_FACTORY);
        final Subparser basicParser = realtimeParser.addSubparsers().addParser("basic").setDefault(FACTORY, BASIC_AUTH_SPOUT_FACTORY);

        oauthParser.addArgument("-a", "--accessToken").required(true);
        oauthParser.addArgument("-s", "--accessTokenSecret").required(true);
        oauthParser.addArgument("-c", "--consumerKey").required(true);
        oauthParser.addArgument("-e", "--consumerSecret").required(true);

        basicParser.addArgument("-t", "--track").required(true);
        basicParser.addArgument("-u", "--username").required(true);
        basicParser.addArgument("-p", "--password").required(true);

        stubParser.addArgument("-f", "--file").required(true);

        try {
            final Namespace namespace = parser.parseArgs(args);
            final SpoutFactory spoutFactory = (SpoutFactory) namespace.get(FACTORY);
            final IRichSpout twitterSpout = spoutFactory.create(namespace);
            final StormTopology topology = createTopology(twitterSpout);
            executeTopology(topology);
        } catch (final ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (final Exception ex) {
            throw new RuntimeException("error while running topology", ex);
        }
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