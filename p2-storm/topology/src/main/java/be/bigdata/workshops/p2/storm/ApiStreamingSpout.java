package be.bigdata.workshops.p2.storm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ApiStreamingSpout extends BaseRichSpout {

    static String STREAMING_API_URL = "https://stream.twitter.com/1/statuses/filter.json?track=";
    private String track;
    private String user;
    private String password;
    private DefaultHttpClient client;
    private SpoutOutputCollector collector;
    private UsernamePasswordCredentials credentials;
    private BasicCredentialsProvider credentialProvider;

    LinkedBlockingQueue<String> tweets = new LinkedBlockingQueue<String>();

    static Logger LOG = Logger.getLogger(ApiStreamingSpout.class);
    static JSONParser jsonParser = new JSONParser();

    @Override
    public void nextTuple() {
        /*
         * Create the client call
         */
        client = new DefaultHttpClient();
        client.setCredentialsProvider(credentialProvider);
        final HttpGet get = new HttpGet(STREAMING_API_URL + track);
        HttpResponse response;
        try {
            // Execute
            response = client.execute(get);
            final StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == 200) {
                final InputStream inputStream = response.getEntity().getContent();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String in;
                // Read line by line
                while ((in = reader.readLine()) != null) {
                    try {
                        // Parse and emit
                        final Object json = jsonParser.parse(in);
                        System.out.println(json);
                        collector.emit(new Values(track, json));
                    } catch (final ParseException e) {
                        LOG.error("Error parsing message from twitter", e);
                    }
                }
            } else {
                LOG.warn("Did not receive 200 OK from Twitter. Perhaps we are rate limited? Reason: "
                    + status.getReasonPhrase());
                try {
                    Thread.sleep(10000);
                } catch (final InterruptedException e1) {
                }
            }
        } catch (final IOException e) {
            LOG.error("Error in communication with twitter api [" + get.getURI().toString() + "]");
            try {
                Thread.sleep(10000);
            } catch (final InterruptedException e1) {
            }
        }
    }

    @Override
    public void open(final Map conf, final TopologyContext context, final SpoutOutputCollector collector) {
        final int spoutsSize = context.getComponentTasks(context.getThisComponentId()).size();
        final int myIdx = context.getThisTaskIndex();
        final String[] tracks = ((String)conf.get("track")).split(",");
        final StringBuffer tracksBuffer = new StringBuffer();
        for (int i = 0; i < tracks.length; i++) {
            if (i % spoutsSize == myIdx) {
                tracksBuffer.append(",");
                tracksBuffer.append(tracks[i]);
            }
        }

        if (tracksBuffer.length() == 0) {
            throw new RuntimeException("No track found for spout" + " [spoutsSize:" + spoutsSize + ", tracks:"
                + tracks.length + "] the amount" + " of tracks must be more then the spout paralellism");
        }

        track = tracksBuffer.substring(1).toString();

        user = (String)conf.get("user");
        password = (String)conf.get("password");

        credentials = new UsernamePasswordCredentials(user, password);
        credentialProvider = new BasicCredentialsProvider();
        credentialProvider.setCredentials(AuthScope.ANY, credentials);
        this.collector = collector;
    }

    @Override
    public void declareOutputFields(final OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("track", "tweet"));
    }

}