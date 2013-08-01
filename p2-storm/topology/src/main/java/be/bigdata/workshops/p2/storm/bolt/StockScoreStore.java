package be.bigdata.workshops.p2.storm.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;

import java.util.Map;

public class StockScoreStore extends BaseBasicBolt {

    private String redisServer;
    private Integer redisPort = 6379;
    private String setName;
    private RedisConnection<String, String> redis;

    public StockScoreStore(String redisServer, Integer redisPort, String setName) {
        this.redisServer = redisServer;
        this.setName = setName;
        this.redisPort = redisPort;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        RedisClient client = new RedisClient(redisServer, redisPort);
        redis = client.connect();
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        redis.zadd(setName, tuple.getDoubleByField("score"), tuple.getStringByField("stock"));
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {   }

}
