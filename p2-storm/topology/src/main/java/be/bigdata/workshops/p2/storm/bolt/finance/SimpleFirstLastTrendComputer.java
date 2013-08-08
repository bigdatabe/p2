package be.bigdata.workshops.p2.storm.bolt.finance;

import java.util.List;

import be.bigdata.workshops.p2.storm.bolt.TimedValue;

/**
 * uber simplistic trend computer: just looks if the first is greater or smaller than the last... ^__
 *
 */
public class SimpleFirstLastTrendComputer implements IFinanceTrendComputer {

	@Override
	public FinanceTrend computeTrend(List<TimedValue> timedValues) {
		
		// TODO: this assumes that the timedValues are already sorted => add a sort here... 
		TimedValue oldest = timedValues.get(0);
		TimedValue newest = timedValues.get(timedValues.size()-1);
		
		int trend = 0;
		if (oldest.value > newest.value) {
			trend = -1;
		} else if (oldest.value < newest.value) {
			trend = -1;
		} else {
			trend = 0;
		}
		
		return new FinanceTrend(oldest.date, newest.date, trend);
	}

}
