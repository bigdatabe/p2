package be.bigdata.workshops.p2.storm.factories;

import backtype.storm.topology.IRichSpout;
import be.bigdata.workshops.p2.storm.ApiStreamingSpoutStub;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 1:51 PM
 */
public class StubSpoutFactory implements SpoutFactory {
    private static final Logger LOG = Logger.getLogger(OAuthSpoutFactory.class);

    @Override
    public IRichSpout create(Namespace namespace) {
        final String filePath = namespace.getString("file");
        LOG.debug("Creating spout for file path " + filePath);
        IRichSpout spout = createStubbedTwitterSpout(filePath);
        return spout;
    }

    static IRichSpout createStubbedTwitterSpout(final String filePath) {
        return new ApiStreamingSpoutStub(filePath);
    }

}
