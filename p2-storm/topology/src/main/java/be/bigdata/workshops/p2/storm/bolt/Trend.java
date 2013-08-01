package be.bigdata.workshops.p2.storm.bolt;

import java.util.Date;

public class Trend {

	private Date startTime;
	private Date endTime;
	private Integer trend;

	public Trend() {
		// TODO Auto-generated constructor stub
	}

	public Trend(Date startTime, Date endTime, Integer trend) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.trend = trend;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
