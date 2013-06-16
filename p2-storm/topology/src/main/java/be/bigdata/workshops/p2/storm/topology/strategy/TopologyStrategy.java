package be.bigdata.workshops.p2.storm.topology.strategy;

import backtype.storm.generated.StormTopology;
import backtype.storm.topology.IRichSpout;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 4:51 PM
 */
public interface TopologyStrategy {
    /**
     * Create the actual {@link StormTopology}.
     *
     * @param twitterSpout a {@link IRichSpout} compatible with the twitter api output interface (can be realtime or stub
     *            instance).
     * @return the {@link StormTopology}.
     */
    public StormTopology build(IRichSpout twitterSpout);
}
