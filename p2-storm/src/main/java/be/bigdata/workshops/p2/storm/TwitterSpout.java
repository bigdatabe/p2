package be.bigdata.workshops.p2.storm;

import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;

/**
 * Will read and broadcast a twitter stream.
 * 
 * @author dierickx
 */
public class TwitterSpout extends BaseRichSpout {

    @Override
    public void open(final Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
        // TODO Auto-generated method stub

    }

    @Override
    public void nextTuple() {
        // TODO Auto-generated method stub

    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("double", "triple"));
    }

}
