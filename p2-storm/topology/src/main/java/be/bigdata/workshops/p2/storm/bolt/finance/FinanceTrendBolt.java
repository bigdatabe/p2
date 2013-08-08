package be.bigdata.workshops.p2.storm.bolt.finance;

import static be.bigdata.workshops.p2.storm.FinanceTopology.FINANCE_AGGREGATION_PERIOD_PARAM;
import static be.bigdata.workshops.p2.storm.Utils.isTickTuple;

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

/**
 * @author svend
 * 
 *         Aggregates quote values for each quote name and emit quote trends (not necessarily after each quote value, the exact aggregation
 *         and trend emission policy is delegated to a set {@link IFinanceQuoteAccumulator} and {@link IFinanceTrendComputer}
 * 
 */
public class FinanceTrendBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;
	
	private OutputCollector collector;
	private Long financeWindowSize;

	private Map<String, FinanceQuoteAccumulator> trendComputers = new HashMap<>();

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		financeWindowSize = (Long) stormConf.get(FINANCE_AGGREGATION_PERIOD_PARAM);
	}

	@Override
	public void execute(Tuple tuple) {

		if (!isTickTuple(tuple)) {
			String stockName = (String) tuple.getValueByField("stock");
			Double stockPrice = (Double) tuple.getValueByField("value");
			Date date = (Date) tuple.getValueByField("date");
			
			if (!trendComputers.containsKey(stockName)) {
				trendComputers.put(stockName, new FinanceQuoteAccumulator(financeWindowSize, new SimpleFirstLastTrendComputer()));
			}
			
			trendComputers.get(stockName).update(stockPrice, date);
		} 

		// after each quote and at each "tick": check if any aggregators has anything to say
		for (String stockName : trendComputers.keySet()) {
			FinanceTrend trend = trendComputers.get(stockName).getTrend();
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
