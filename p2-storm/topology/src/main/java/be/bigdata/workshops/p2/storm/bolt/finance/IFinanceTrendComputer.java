package be.bigdata.workshops.p2.storm.bolt.finance;

import java.util.List;

import be.bigdata.workshops.p2.storm.bolt.TimedValue;


public interface IFinanceTrendComputer {

	/**
	 * computes the finance trend corresponding to those timed quotations 
	 */
	public FinanceTrend computeTrend(List<TimedValue> timedValues);
	
}
