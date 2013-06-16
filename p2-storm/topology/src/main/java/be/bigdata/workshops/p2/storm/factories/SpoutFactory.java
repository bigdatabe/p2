package be.bigdata.workshops.p2.storm.factories;

import backtype.storm.topology.IRichSpout;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Created with IntelliJ IDEA.
 * User: jens
 * Date: 6/16/13
 * Time: 1:52 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SpoutFactory {
    public IRichSpout create(Namespace namespace);
}
