package be.bigdata.workshops.p2.storm.factories;

import backtype.storm.Config;
import backtype.storm.topology.IRichSpout;
import be.bigdata.workshops.p2.storm.ApiStreamingSpout;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 1:54 PM
 */
public class BasicAuthSpoutFactory implements SpoutFactory {
    private static final Logger LOG = Logger.getLogger(BasicAuthSpoutFactory.class);

    @Override
    public IRichSpout create(Namespace namespace, Config conf) {
        LOG.info("Processing tweets in real-time using basic authentication");

        final String username = namespace.getString("username");
        final String password = namespace.getString("password");
        final String track = namespace.getString("track");

        conf.put("user", username);
        conf.put("password", password);
        conf.put("track", track);

        return new ApiStreamingSpout();
    }
}
