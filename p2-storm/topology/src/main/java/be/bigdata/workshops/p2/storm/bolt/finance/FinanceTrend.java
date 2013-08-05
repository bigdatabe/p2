package be.bigdata.workshops.p2.storm.bolt.finance;

import java.util.Date;

public class FinanceTrend {

	private Date startTime;
	private Date endTime;
	private Integer trend;

	public FinanceTrend(Date startTime, Date endTime, Integer trend) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.trend = trend;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getTrend() {
		return trend;
	}

	public void setTrend(Integer trend) {
		this.trend = trend;
	}

}
