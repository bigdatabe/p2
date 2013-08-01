package be.bigdata.workshops.p2.storm.bolt;

import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

/**
 * Simple input logger bolt.
 * 
 * @author dierickx
 */
public class DebugBolt extends BaseRichBolt {
    private static final long serialVersionUID = 1037052250323137715L;
    private static Logger LOGGER = Logger.getLogger(DebugBolt.class);
    private OutputCollector coll ; 

    @Override
    public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
        LOGGER.info("Preparing debug bolt.");
    }

    @Override
    public void execute(final Tuple input) {
        for (final String field : input.getFields()) {
            LOGGER.info(String.format("%s = '%s'", field, input.getValueByField(field)));
        }
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub
    }
}
