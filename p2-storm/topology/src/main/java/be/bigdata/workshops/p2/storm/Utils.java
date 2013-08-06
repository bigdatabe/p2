package be.bigdata.workshops.p2.storm;

import backtype.storm.tuple.Tuple;

public class Utils {

	/**
	 * @return true if this is "tick" tuple emitted by the Storm framework
	 */
	public static boolean isTickTuple(Tuple tuple) {
		return tuple != null &&  "__tick".equals(tuple.getSourceStreamId());
	}

}
