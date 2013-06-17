package be.bigdata.workshops.p2.storm.execution.strategy;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.utils.Utils;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 6:59 PM
 */
public class GenericExecutionStrategy implements ExecutionStrategy {
    private final int duration;

    public GenericExecutionStrategy(int duration) {
        this.duration = duration;
    }

    @Override
    public void execute(StormTopology topology, Config conf) {
        final LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("twitter-test", conf, topology);

        Utils.sleep(duration * 1000);
        cluster.killTopology("twitter-test");
        cluster.shutdown();
    }
}
