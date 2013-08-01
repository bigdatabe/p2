package be.bigdata.workshops.p2.storm.bolt;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TrendComputer {

	private final Long financeWindowSize;
	

	private List<TimedValue> timedValues = new LinkedList<>();
	

	public TrendComputer(Long financeWindowSize) {
		this.financeWindowSize = financeWindowSize;
	}

	public void addData(Double stockPrice, Date date) {
		
		// TODO: if tuples are receivd out of order, we should keep the first and last tuple somehow...
		timedValues.add(new  TimedValue(date, stockPrice));
	}

	public boolean isReady() {
		if (timedValues.size() < 2) {
			return false;
		}
		return timedValues.get(timedValues.size()-1).date.getTime() - timedValues.get(0).date.getTime() >= financeWindowSize;
	}

	public Trend closeAndGetTrend() {

		
		// TODO: compute a real trend here: atm we just use first and last vlue
		if (isReady()) {
			
			System.out.println("closing");
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
			
			timedValues.clear();
			
			return new Trend(oldest.date, newest.date, trend);
			
		} else {
			throw new RuntimeException("this is a bug: should only call this if isReady, errr, is ready...");
		}
	}
	
	
	private class TimedValue {
		public TimedValue(Date date, Double value) {
			super();
			this.date = date;
			this.value = value;
		}
		private final Date date;
		private final Double value;
	}
	

}
