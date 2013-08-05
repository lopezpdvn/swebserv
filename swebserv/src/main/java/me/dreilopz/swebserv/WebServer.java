/**
 * 
 */
package me.dreilopz.swebserv;

public interface WebServer {
	void setServerPrng(ExponentialPrng serverPrng);
	void simulate();
	double getMeanWaitTime();
	double getWaitTimeStddev();
	double getDropRatio();
	long getNReq();
	long getMaxReqQueueLength();
	void setMaxReqQueueLength(long q);
	void setNReq(long r);
	void setReqFactory(Request reqFactory);
}
