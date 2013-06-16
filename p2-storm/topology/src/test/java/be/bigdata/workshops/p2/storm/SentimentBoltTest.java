package be.bigdata.workshops.p2.storm;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import backtype.storm.task.TopologyContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * JUnit Theory which can be used to test the sentiment scoring algorithm on specific cases.
 * @author dominiek
 *
 */
@RunWith(Theories.class)
public class SentimentBoltTest {
	
	/**
	 * Utility class for helping out with setting up the parameters for the tests.
	 * @author dominiek
	 *
	 */
	static class Comparison {
		public String negative;
		public String positive;
		
		public Comparison(String negative, String positive) {
			this.negative = negative;
			this.positive = positive;
		}
	}

	private SentimentBolt sentimentBolt;
	
	@DataPoints
	 public static Comparison[] data() {
		// test set is constructed here.
		// first argument is a negative sentiment tweet, second a positive one.
		return Lists.newArrayList(
				new Comparison("Keep your eye on $AAPL http://on.mktw.net/112Jr5c  Currently down 2.2% at 416.7", "$MSFT wins patent on wearable device that transfers data through your body http://stks.co/sGm9"),
				new Comparison("Microsoft closes down 4.5% after Goldman Sachs slaps software maker with a 'sell' rating citing weak PC sales: http://fxn.ws/YOCHtB  $MSFT", "This is big news RT @ahess247: Microsoft Has Brought Office 365 to Apple's iOS. http://dthin.gs/10hU8p6  $AAPL $MSFT")
				
				).toArray(new Comparison[0]);
	}

	@Before
	public void setUp() {
		sentimentBolt = new SentimentBolt();
		Map<String, String> stormConf = Maps.newHashMap();
		TopologyContext context = null; // mock would be more appropriate but we don't use context (yet).
		stormConf.put(SentimentBolt.SENTIMENT_FILE, "sentiment_scores.txt");
		sentimentBolt.prepare(stormConf , context);
	}
	
	@Theory
	public void testPositiveHasPositiveSentimentScore(Comparison comparison) {
		assertTrue(sentimentBolt.scoreSentiment(comparison.positive) > 0);
	}
	
	@Theory
	public void testNegativeHasNegativeSentimentScore(Comparison comparison) {
		assertTrue(sentimentBolt.scoreSentiment(comparison.negative) < 0);
	}
	
	@Theory
	public void testCompareSentimentScore(Comparison comparison) {
		int scoreNegative = sentimentBolt.scoreSentiment(comparison.negative);
		int scorePositive = sentimentBolt.scoreSentiment(comparison.positive);
		assertTrue(scoreNegative < scorePositive);
	}
}
