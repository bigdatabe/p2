package be.bigdata.workshops.p2.storm;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import org.apache.log4j.Logger;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Map;

public class ApiStreamingSpoutStub extends BaseRichSpout {
	private SpoutOutputCollector collector;

	private BufferedReader bufferReader;
    private String track;

	static Logger LOG = Logger.getLogger(ApiStreamingSpoutStub.class);
	static JSONParser jsonParser = new JSONParser();

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
        final String filePath = (String) conf.get("file");
        track = (String) conf.get("track");
        LOG.info("Opening file at " + filePath);

		try {
			this.bufferReader = new BufferedReader(
					new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void nextTuple() {
		try {
			// TODO Auto-generated catch block
			Utils.sleep(10);
			String line = bufferReader.readLine();
			if (line != null) {
                Object json = jsonParser.parse(line);
                collector.emit(new Values(track, json));
            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("track", "tweet"));
	}
	
	@Override
	public void close() {
		try {
			bufferReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.close();
	}
}