package be.bigdata.workshops.p2.storm.execution.strategy;

import backtype.storm.Config;
import backtype.storm.generated.StormTopology;

/**
 * User: jens
 * Date: 6/16/13
 * Time: 6:47 PM
 */
public interface ExecutionStrategy {
    public void execute(StormTopology topology, Config conf);
}
