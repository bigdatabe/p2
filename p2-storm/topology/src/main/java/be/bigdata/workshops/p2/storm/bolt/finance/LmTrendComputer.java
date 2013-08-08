package be.bigdata.workshops.p2.storm.bolt.finance;

import java.util.Date;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import be.bigdata.workshops.p2.storm.bolt.TimedValue;

/**
 * @author svend
 * 
 * Computes a global trend in {-1, 0, 1} based on a simple least square linear model between quotes value and time (in sec).
 * 
 *  The implementation assumes the values are sorted chronologically
 *
 */
public class LmTrendComputer implements IFinanceTrendComputer{

	/**
	 * any corr coef whose absolute value is smaller than that yields a trend value 0
	 */
	public static Double SMALL_SLOPE_TRESHOLD = .25;
	
	@Override
	public FinanceTrend computeTrend(List<TimedValue> timedValues) {
		
		if (timedValues == null) {
			return null;
		}
		
		TimedValue first = timedValues.get(0);
		Long firstTime = first.date.getTime();
		Double firstVal = first.value;
		
		if (timedValues.size() == 1) {
			return new FinanceTrend(first.date, first.date, 0);
		}
		
		if (timedValues.size() == 2) {
			TimedValue second = timedValues.get(1);
			if (second.value == firstVal) {
				return new FinanceTrend(first.date, second.date, 0);
			} else if (second.value < firstVal) {
				return new FinanceTrend(first.date, second.date, -1);
			} else {
				return new FinanceTrend(first.date, second.date, +1);
			}
		}
		
		// more than 2 values => builds a simple least square linear model with all values shifted so that first value is (time==0, value==0), to avoid computing imprecision on big numbers
		// TODO: currency is not taken into account => small value currencies will systematically have higher slopes
		SimpleRegression linearModel = new SimpleRegression();		
		for (TimedValue quote : timedValues) {
			linearModel.addData((quote.date.getTime() - firstTime)/ 1000 , quote.value - firstVal);
		}
		
		Date firstDate = first.date;
		Date lastDate = timedValues.get(timedValues.size() -1 ).date;
		
		Double slope = linearModel.getSlope();
		if (Math.abs(slope) < SMALL_SLOPE_TRESHOLD) {
			return new FinanceTrend(firstDate, lastDate, 0);
		} else if (slope < 0) {
			return new FinanceTrend(firstDate, lastDate, -1);
		} else {
			return new FinanceTrend(firstDate, lastDate, +1);
		}
	}

}
