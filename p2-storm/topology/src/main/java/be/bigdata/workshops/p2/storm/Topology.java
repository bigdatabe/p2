package be.bigdata.workshops.p2.storm;

import be.bigdata.workshops.p2.storm.execution.strategy.ExecutionStrategy;
import be.bigdata.workshops.p2.storm.execution.strategy.GenericExecutionStrategy;
import be.bigdata.workshops.p2.storm.execution.strategy.InfiniteExecutionStrategy;
import be.bigdata.workshops.p2.storm.topology.strategy.PlainTopologyStrategy;
import be.bigdata.workshops.p2.storm.factories.BasicAuthSpoutFactory;
import be.bigdata.workshops.p2.storm.factories.OAuthSpoutFactory;
import be.bigdata.workshops.p2.storm.factories.SpoutFactory;
import be.bigdata.workshops.p2.storm.factories.StubSpoutFactory;
import be.bigdata.workshops.p2.storm.topology.strategy.DebugTopologyStrategy;
import be.bigdata.workshops.p2.storm.topology.strategy.TopologyStrategy;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.*;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;
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
        Subparsers subparsers = parser.addSubparsers();
        final Subparser realTimeParser = subparsers.addParser("realtime");

        final Subparser stubParser = subparsers.addParser("stub").setDefault(FACTORY, STUB_SPOUT_FACTORY);
        Subparsers realTimeSubparsers = realTimeParser.addSubparsers();
        final Subparser oauthParser = realTimeSubparsers.addParser("oauth").setDefault(FACTORY, OAUTH_SPOUT_FACTORY);
        final Subparser basicParser = realTimeSubparsers.addParser("basic").setDefault(FACTORY, BASIC_AUTH_SPOUT_FACTORY);

        parser.addArgument("-d", "--debug").help("Enable the debug bolt").action(Arguments.storeTrue()).setDefault(false);
        parser.addArgument("-t", "--time").help("Amount of seconds to run. Choose a negative number to run infinitely.").type(Integer.class).setDefault(30);
        ArgumentGroup sentimentGroup = parser.addArgumentGroup("Sentiment Analysis");
        sentimentGroup.addArgument("--sentiment_file").help("Sentiment file").setDefault("sentiment_scores.txt");

        oauthParser.addArgument("-a", "--accessToken").required(true);
        oauthParser.addArgument("-s", "--accessTokenSecret").required(true);
        oauthParser.addArgument("-c", "--consumerKey").required(true);
        oauthParser.addArgument("-e", "--consumerSecret").required(true);

        basicParser.addArgument("-t", "--track").required(true);
        basicParser.addArgument("-u", "--username").required(true);
        basicParser.addArgument("-p", "--password").required(true);

        stubParser.addArgument("-f", "--file")
                .required(true)
                .type(Arguments.fileType().acceptSystemIn().verifyCanRead())
                .setDefault("-");

        try {
            final Namespace namespace = parser.parseArgs(args);
            final Boolean debug = namespace.getBoolean("debug");
            final Config conf = new Config();

            final SpoutFactory spoutFactory = (SpoutFactory) namespace.get(FACTORY);
            final IRichSpout twitterSpout = spoutFactory.create(namespace, conf);

            final TopologyStrategy topologyStrategy;

            if (debug) {
                topologyStrategy = new DebugTopologyStrategy();
            } else {
                topologyStrategy = new PlainTopologyStrategy();
            }

            final StormTopology topology = topologyStrategy.build(twitterSpout);

            final String sentimentFile = namespace.getString("sentiment_file");
            conf.put("sentiment_file", sentimentFile);
            final int duration = namespace.getInt("time");
            final ExecutionStrategy executionStrategy;
            if (duration < 0) {
                executionStrategy = new InfiniteExecutionStrategy();
            } else {
                executionStrategy = new GenericExecutionStrategy(duration);
            }
            executionStrategy.execute(topology, conf);
        } catch (final ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        } catch (final Exception ex) {
            throw new RuntimeException("error while running topology", ex);
        }
    }
}