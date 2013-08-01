package be.bigdata.workshops.p2.storm.bolt;

import java.util.Date;

public class TrendComputer {

	private final Long financeWindowSize;

	public TrendComputer(Long financeWindowSize) {
		this.financeWindowSize = financeWindowSize;
	}

	public void addData(Double stockPrice, Date date) {
		// TODO Auto-generated method stub
		
	}

	public boolean isReady() {
		// TODO 
		return true;
	}

	public Trend closeAndGetTrend() {
		return new Trend(new Date(), new Date(), 0);
	}

}
