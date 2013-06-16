package be.bigdata.workshops.p2.storm.factories;

import backtype.storm.topology.IRichSpout;
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
    public IRichSpout create(Namespace namespace) {
        LOG.info("Processing tweets in real-time using basic authentication");
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
