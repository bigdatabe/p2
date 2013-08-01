package be.bigdata.workshops.p2.storm.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.google.common.base.Splitter;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Bolt that processes incoming tweets, performs some basic sentiment analysis
 * on the tweet text, and emits the tracking keyword, the original tweet and a
 * basic sentiment (String value "+" or "-") to the output collector for further
 * processing.
 *
 * @author Olivier Van Laere <oliviervanlaere@gmail.com>
 */
public class SentimentBolt extends BaseBasicBolt{

	/**
	 * Key in the Storm configuration for the sentiment file filename.
	 */
	public static final String SENTIMENT_FILE = "sentiment_file";

	private static final long serialVersionUID = -9213384477488358455L;

  /**
   * Constant for the positive sentiment
   */
  public static final String SENTIMENT_POSITIVE = "+";

  /**
   * Constant for the negative sentiment
   */
  public static String SENTIMENT_NEGATIVE = "-";

  /**
   * Constant for the neutral sentiment - just for debugging purpose
   */
  public static String SENTIMENT_NEUTRAL = "o";


  /**
   * Map holding some word-score pairs for sentiment analysis.
   */
  private Map<String, Integer> sentiment_scores = new HashMap<String, Integer>();

  /**
   * Logger.
   */
  private static Logger LOG = Logger.getLogger(SentimentBolt.class);

  /**
   * Actual bolt processing code.
   * @param input Input Tuple from a Spout.
   * @param collector The outputcollector that collects the output of this
   * calculation.
   */
  @Override
  public void execute(Tuple input, BasicOutputCollector collector) {
    // Fetch the tracking keyword from the input tuple
    String track = (String)input.getValueByField("track");
    // Fetch the tweet text embedded in the "tweet" field in the input tuple
    String tweetText = (String)input.getValueByField("tweet");
    // Convert it to lowercase - the sentiment words are all lowercase
    int score = scoreSentiment(tweetText.toLowerCase());
      // Init the actual sentiment for this tweet
      String sentiment = SENTIMENT_NEUTRAL;
      if (score > 0)
        sentiment = SENTIMENT_POSITIVE;
      else if (score < 0)
        sentiment = SENTIMENT_NEGATIVE;
      // If there was a non-neutral
      if (!sentiment.equals(SENTIMENT_NEUTRAL)) {
        // Emit data to the output collector for the next processing phase
        collector.emit(new Values(track, tweetText, sentiment));
      }
        // Debug info
      System.out.println("SENTIMENT" + sentiment + " " + tweetText);
  }

  /* package */ int scoreSentiment(String text) {
      // Init the tweet score
      int score = 0;
      // For each of the words
      for (String word : Splitter.on(' ').split(text)) {
        // If the word is in the sentiment map
        if (sentiment_scores.containsKey(word)) {
          // Increment the score by the score from the map for this word
          score += sentiment_scores.get(word);
        }
      }

      return score;
  }

  /**
   * Bolt preparation.
   */
  @Override
  public void prepare(Map stormConf, TopologyContext context) {
    // Fetch the URI for the sentiment file
    String sentimentFile = ((String) stormConf.get(SENTIMENT_FILE));
    // Normally this should go via classloading/jarloading/resources?

    // Init the reader
    BufferedReader file = null;
    try {
      // Load the sentiment file
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
      LOG.error("IOException: " + e.getMessage());
    }
    finally {
      try {
        if (file != null)
          file.close();
      }
      catch (IOException e) {
        // This should not happen...
        LOG.error("Dying over here...");
      }
    }
    LOG.info("Sentiment words loaded: " + sentiment_scores.size());
  }

  /**
   * Declare the output fields of this bolt.
   * @param declarer
   */
  @Override
  public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declare(new Fields("track", "tweet", "sentiment"));
  }

  @Override
  public void cleanup() {
    // Not implemented.
  }
}