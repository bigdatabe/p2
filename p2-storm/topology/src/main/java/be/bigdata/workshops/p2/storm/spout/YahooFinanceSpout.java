package be.bigdata.workshops.p2.storm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

/**
 * This class imlements a Spout that polls yahoo finance
 */
public class YahooFinanceSpout extends BaseRichSpout {

    private static Logger logger = Logger.getLogger(YahooFinanceSpout.class);

    SpoutOutputCollector outputCollector;
    Random _rand;
    protected ArrayList<String> stocks;

    // http://cliffngan.net/a/13
    protected String yahooResponseFormat = "sl1d1t1cv";

    // "http://finance.yahoo.com/d/quotes.csv?s=DATA&f=snl1d1t1cv&e=.csv"
    protected String yahooService = "http://finance.yahoo.com/d/quotes.csv?s=%s&f=%s&e=.csv";

    // Sleep time when between calles to the Spout
    protected int sleep = 1000;


    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        outputCollector = collector;

        // Initialize the stocks that need to be polled
        stocks = Lists.newArrayList();
        stocks.add("FB");
        stocks.add("DATA");
        stocks.add("AAPL");

    }

    @Override
    public void nextTuple() {
        Utils.sleep(sleep);
        int idx = new Double(Math.random() * stocks.size()).intValue(); // generate random index
        String stock = stocks.get(idx);

        String query = String.format(yahooService, stock, yahooResponseFormat);
        try {

            URL url = new URL(query);
            String response = IOUtils.toString(url.openStream()).replaceAll("\"", "");
            String[] lSplit = response.split(",");
            String stockName = lSplit[0];
            double stockPrice = Double.parseDouble(lSplit[1]);
            String date = lSplit[2];
            String time = lSplit[3];

            outputCollector.emit(new Values(stockName, stockPrice, date, time));
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("stock", "value", "date", "time"));
    }

}