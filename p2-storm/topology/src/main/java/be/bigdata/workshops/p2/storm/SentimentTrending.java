package be.bigdata.workshops.p2.storm;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SentimentTrending extends BaseBasicBolt{

	Map<String, Integer> positives = new HashMap<String, Integer>();
	Map<String, Integer> negatives = new HashMap<String, Integer>();

	@Override
	public void cleanup() {
	}
 
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String track = (String)input.getValueByField("track");
		String sentiment = (String) input.getValueByField("sentiment");

		if ("+".equals(sentiment)) {
			add(track, sentiment);
		}
		else if ("-".equals(sentiment)) {
			add(track, sentiment);
		}
	}

	private void add(String track, String sentiment) {
		if(!positives.containsKey(track)){
			positives.put(track, 0);
			negatives.put(track, 0);
		}

		if ("+".equals(sentiment)) {
			Integer last = positives.get(track);
			positives.put(track, last + 1);
		}
		else if ("-".equals(sentiment)) {
			Integer last = positives.get(track);
			positives.put(track, last + 1);
		}
		else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Map<String, Integer> oldPositives = new HashMap<String, Integer>(positives);
				Map<String, Integer> oldNegatives = new HashMap<String, Integer>(negatives);
				positives.clear();
				negatives.clear();

				for(Map.Entry<String, Integer> entry : oldPositives.entrySet()){
					String key = entry.getKey();
					Integer positiveCount = entry.getValue();
					Integer negativeCount = oldNegatives.get(key);

					System.out.println(String.format("%1$s: %2$s, %3$s", key, positiveCount, negativeCount));
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