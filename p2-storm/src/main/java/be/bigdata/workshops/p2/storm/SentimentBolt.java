package be.bigdata.workshops.p2.storm;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SentimentBolt extends BaseBasicBolt{

	Map<String, Integer> sentiment_scores = new HashMap<String, Integer>();
    
    Map<String, String> keyword_sentiment = new HashMap<String, String>();

	@Override
	public void cleanup() {

	}
 
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		JSONObject json = (JSONObject)input.getValueByField("tweet");
		if(json.containsKey("text")){
			String text = ((String)json.get("text")).toLowerCase();
            // Init the tweet score
            int score = 0;
            // Split the text by spaces
            String [] pieces = text.split(" ");
            for (String word : pieces) {
              // If the word is in the sentiment map
              if (sentiment_scores.containsKey(word)) {
                score += sentiment_scores.get(word);
              }
            }
            if (score > 0) {
              keyword_sentiment.put(((String)json.get("text")), "+");
            }
            else if (score < 0) {
              keyword_sentiment.put(((String)json.get("text")), "-");
            }
		}
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
      // Fetch the URI for the sentiment file
      String sentimentFile = ((String) stormConf.get("sentiment_file"));
      
      BufferedReader file = null;
      try {
        file = new BufferedReader(new FileReader(sentimentFile));
        // read all the lines in the sentiment file
        String line = file.readLine();
        // While there are liens
        while (line != null) {
          // Split word - score (tab separated)
          String [] pieces = line.split("\t");
          // Parse the score
          int score = Integer.parseInt(pieces[1]);
          // Put this stuff in the map
          sentiment_scores.put(pieces[0], score);
          line = file.readLine();
        }
      }
      catch (IOException e) {
        System.err.println("IOException: " + e.getMessage());
      }
      finally {
        try {
          if (file != null)
            file.close();
        }
        catch (IOException e) {
          System.err.println("Dying over here...");
        }
      }
      
      // Output - keyword \t sentiment (String: +/-)
      TimerTask task = new TimerTask() {

          @Override
          public void run() {
              Map<String, String> oldMap = new HashMap<String, String>(keyword_sentiment);
              keyword_sentiment.clear();
              for(Map.Entry<String, String> entry : oldMap.entrySet()){
                  System.out.println(entry.getKey()+"\t"+entry.getValue());
              }
          }

      };
      Timer t = new Timer();
      t.scheduleAtFixedRate(task, 10000, 10000);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
