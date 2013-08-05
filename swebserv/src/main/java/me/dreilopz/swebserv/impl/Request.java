package me.dreilopz.swebserv.impl;

import me.dreilopz.swebserv.ExponentialPrng;

class Request implements me.dreilopz.swebserv.Request {
	private double startTime;
	private long reqNo;
	private int reqType;
	private static ExponentialPrng reqPrng;
	
	private Request(){}
	
	public Request createInstance() {
		return new Request();
	}

	public String toString() {
		return "Request " + reqNo + " with type " + reqType + " with start time " + startTime;
	}

	public void setStartTime(double startTime) {
		this.startTime = startTime;
	}

	public void setReqNo(long reqNo) {
		this.reqNo = reqNo;
	}

	public void setReqType(int reqType) {
		this.reqType = reqType;
	}

	public double getStartTime() {
		return startTime;
	}

	public void setReqPrng(ExponentialPrng reqPrng) {
		Request.reqPrng = reqPrng;
	}
	
	public double getArrivalTime() {
		return reqPrng.rand();
	}
}
