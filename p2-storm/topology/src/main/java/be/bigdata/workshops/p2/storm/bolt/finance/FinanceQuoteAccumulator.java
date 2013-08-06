package be.bigdata.workshops.p2.storm.bolt.finance;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import be.bigdata.workshops.p2.storm.bolt.TimedValue;

/**
 * Simple accumulator that just waits for at least "financeWindowSize" milliseconds of tuples, then triggers an aggregation and clear the memory => this one never remembers anything about history.
 * 
 *  WARNING: the whole accumulation is happening in memory, in this java instance : this is not yet based on the State mechanism of Storm!
 */
public class FinanceQuoteAccumulator implements IFinanceQuoteAccumulator {

	private final Long financeWindowSize;
	private final IFinanceTrendComputer trendComputer;

	private List<TimedValue> timedValues = new LinkedList<>();
	

	public FinanceQuoteAccumulator(Long financeWindowSize, IFinanceTrendComputer trendComputer) {
		this.financeWindowSize = financeWindowSize;
		this.trendComputer = trendComputer;
	}

	/* (non-Javadoc)
	 * @see be.bigdata.workshops.p2.storm.bolt.IFinanceQuoteAccumulator#addData(java.lang.Double, java.util.Date)
	 */
	@Override
	public void update(Double stockPrice, Date date) {
		timedValues.add(new  TimedValue(date, stockPrice));
	}

	/* (non-Javadoc)
	 * @see be.bigdata.workshops.p2.storm.bolt.IFinanceQuoteAccumulator#closeAndGetTrend()
	 */
	@Override
	public FinanceTrend getTrend() {
		if (isTrendReady()) {
			final FinanceTrend trend = trendComputer.computeTrend(timedValues);
			timedValues.clear();
			return trend;
		} else {
			return null;
		}
	}
	
	
	/**
	 * @return true if the size of the current accumulated time window is >= the parameter received in the constructor 
	 */
	protected boolean isTrendReady() {
		if (timedValues.size() < 2) {
			return false;
		}
		
		// TODO: this assumes the list of values is always sorted, but we actually sort it nowwhere => depends on the spout => fix that
		Date firstDate = timedValues.get(0).date;
		Date lastDate = timedValues.get(timedValues.size()-1).date;
		
		Date now = new Date();
		if (now.after(lastDate)){
			lastDate = now;
		}
		
		return lastDate.getTime() - firstDate.getTime() >= financeWindowSize;
	}

	
}
