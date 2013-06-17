package be.bigdata.workshops.p2.storm.execution.strategy;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 6:49 PM
 */
public class InfiniteExecutionStrategy implements ExecutionStrategy {
    @Override
    public void execute(StormTopology topology, Config conf) {
        final LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("twitter-test", conf, topology);

        // TODO: Find a way to shut down cleanly...
    }
}
