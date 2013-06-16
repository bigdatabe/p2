package be.bigdata.workshops.p2.storm.factories;

import backtype.storm.Config;
import backtype.storm.topology.IRichSpout;
import be.bigdata.workshops.p2.storm.ApiStreamingSpoutStub;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 1:51 PM
 */
public class StubSpoutFactory implements SpoutFactory {
    private static final Logger LOG = Logger.getLogger(OAuthSpoutFactory.class);

    @Override
    public IRichSpout create(Namespace namespace, Config conf) {
        final File file = (File) namespace.get("file");
        conf.put("file", file.getAbsolutePath());
        LOG.debug("Creating spout for file path " + file.getAbsolutePath());
        return new ApiStreamingSpoutStub();
    }
}
