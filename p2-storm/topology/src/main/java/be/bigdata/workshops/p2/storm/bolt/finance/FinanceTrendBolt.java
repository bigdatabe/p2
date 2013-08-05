package be.bigdata.workshops.p2.storm.bolt.finance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import be.bigdata.workshops.p2.storm.bolt.SimpleFirstLastTrendComputer;

public class FinanceTrendBolt extends BaseRichBolt {

	private OutputCollector collector;
	private Long financeWindowSize;

	private Map<String, FinanceQuoteAccumulator> trendComputers = new HashMap<>();

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		financeWindowSize = (Long) stormConf.get("finance.window.size");
	}

	@Override
	public void execute(Tuple input) {

		String stockName = (String) input.getValueByField("stock");
		Double stockPrice = (Double) input.getValueByField("value");
		Date date = (Date) input.getValueByField("date");

		if (!trendComputers.containsKey(stockName)) {
			trendComputers.put(stockName, new FinanceQuoteAccumulator(financeWindowSize, new SimpleFirstLastTrendComputer()));
		}

		trendComputers.get(stockName).update(stockPrice, date);

		for (IFinanceQuoteAccumulator computer : trendComputers.values()) {
			FinanceTrend trend = computer.getTrend();
			if (trend != null) {
				collector.emit(new Values(stockName, trend.getTrend(), trend.getStartTime(), trend.getEndTime()));
			}
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("stockName", "trend", "startTime", "endTime"));

	}

}
