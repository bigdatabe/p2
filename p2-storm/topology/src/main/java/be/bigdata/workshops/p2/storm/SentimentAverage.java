package be.bigdata.workshops.p2.storm;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class SentimentAverage extends BaseBasicBolt{

	Map<String, SentimentCount> sentiment_dict = new HashMap<String, SentimentCount>();

	@Override
	public void cleanup() {

	}
 
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		
            String ticker = (String) input.getValueByField("ticker");
            int sentiment_score = (Integer) input.getValueByField("score");
            SentimentCount currentCount = sentiment_dict.get(ticker);
            if(currentCount==null){
                currentCount = new SentimentCount();
                sentiment_dict.put(ticker, currentCount);
            }
            currentCount.addSentiment(sentiment_score);
            
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Map<String, SentimentCount> oldMap = new HashMap<String, SentimentCount>(sentiment_dict);
				sentiment_dict.clear();
                                
				for(Map.Entry<String, SentimentCount> entry : oldMap.entrySet()){
                                        SentimentCount sentimentCount = entry.getValue();
					System.out.println(entry.getKey()+": "+(sentimentCount.getAverage()));
				}
			}

		};
		Timer t = new Timer();
		t.scheduleAtFixedRate(task, 60000, 60000);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}