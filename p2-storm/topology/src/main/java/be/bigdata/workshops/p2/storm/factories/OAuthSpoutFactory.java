package be.bigdata.workshops.p2.storm.factories;

import backtype.storm.topology.IRichSpout;
import be.bigdata.workshops.p2.storm.TwitterOAuthSpout;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 1:54 PM
 */
public class OAuthSpoutFactory implements SpoutFactory {
    private static final Logger LOG = Logger.getLogger(OAuthSpoutFactory.class);

    @Override
    public IRichSpout create(Namespace namespace) {
        LOG.info("Processing tweets in real-time using OAuth");
        final String accessToken = namespace.getString("accessToken");
        final String accessTokenSecret = namespace.getString("accessTokenSecret");
        final String consumerKey = namespace.getString("consumerKey");
        final String consumerSecret = namespace.getString("consumerSecret");

        LOG.info("accessToken: " + accessToken);
        LOG.info("accessTokenSecret: " + accessTokenSecret);
        LOG.info("consumerKey: " + consumerKey);
        LOG.info("consumerSecret: " + consumerSecret);

        IRichSpout twitterSpout = createRealtimeTwitterSpout(accessToken, accessTokenSecret, consumerKey, consumerSecret);

        return twitterSpout;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private IRichSpout createRealtimeTwitterSpout(final String accessToken, final String accessTokenSecret,
                                                              final String consumerKey, final String consumerSecret) {
        return new TwitterOAuthSpout(accessToken, accessTokenSecret, consumerKey, consumerSecret);
    }

}
