package be.bigdata.workshops.p2.storm.bolt;

import java.util.Date;

public class TimedValue {
	public final Date date;
	public final Double value;

	public TimedValue(Date date, Double value) {
		super();
		this.date = date;
		this.value = value;
	}
}