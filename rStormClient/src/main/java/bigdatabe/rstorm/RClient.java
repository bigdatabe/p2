package bigdatabe.rstorm;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.mcbrooks.twitter.Status;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class RClient extends BaseRichBolt{
	
	
	public final String R_SERVICE_ENDPOINT = "http://localhost/...";
	
	private OutputCollector collector;

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	public void execute(Tuple tuple) {
		Status tweetStatus = (Status) tuple.getValueByField("tweetStatus");
		String sentiment = sendTweet(tweetStatus);
		
		if (sentiment != null && ! "".equals(sentiment)){
			collector.emit(new Values(sentiment));
		}
	}

	
	
	protected String sendTweet(Status tweetStatus) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(R_SERVICE_ENDPOINT);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonTweet = mapper.writeValueAsString(tweetStatus);
			
			
			HttpParams params = new BasicHttpParams(); 
			params.setParameter("tweet", jsonTweet);
			httpGet.setParams(params);
			
			HttpResponse response1 = httpclient.execute(httpGet);
			
			// TODO: read this from response when format is defined 
			String response = "happy";
			return response;
			
		} catch (Exception e) {
			// TODO: replace this with project exception ...
			throw new RuntimeException("failed to contact R service", e);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("sentiment"));
	}


	
}
