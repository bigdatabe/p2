package be.bigdata.workshops.p2.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class StockJoiner extends BaseRichBolt {

    private Cache<String, List<Double>> cacheBuilder;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, final OutputCollector outputCollector) {

        cacheBuilder = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .removalListener(new RemovalListener<String, List<Double>>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, List<Double>> notification) {
                        if(notification.getValue().size() > 0)
                            outputCollector.emit(new Values(notification.getKey(), rating(notification.getValue())));
                    }
                })
                .build();
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            List<Double> scores = cacheBuilder.get(tuple.getStringByField("stock"), new Callable<List<Double>>() {
                @Override
                public List<Double> call() throws Exception {
                    return new ArrayList<Double>();
                }
            });
            scores.add(tuple.getDoubleByField("score"));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("stock", "score"));
    }

    private Double rating(List<Double> scores) {
        Double sum = 0d;
        for (Double d : scores)
            sum += d;
        return sum / scores.size();
    }
}
