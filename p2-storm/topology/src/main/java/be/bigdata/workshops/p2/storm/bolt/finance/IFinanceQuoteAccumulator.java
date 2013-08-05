package be.bigdata.workshops.p2.storm.bolt.finance;

import java.util.Date;

/**
 * Accumulator of timestamped quotations for one stock name. 
 *
 */
public interface IFinanceQuoteAccumulator {

	/**
	 * Notification that one more stock quote is available  
	 */
	public abstract void update(Double stockPrice, Date date);


	/**
	 * @return the latest {@link FinanceTrend} based on the latest quotes, or null if no trend is available
	 */
	public FinanceTrend getTrend();

}