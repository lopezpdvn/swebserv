/**
 * 
 */
package me.dreilopz.swebserv;

/**
 * @author dreilopz0
 *
 */
public interface WebServer {
	void setUniformPrng(UniformPrng uniformPrng);
	void setServerPrng(ExponentialPrng serverPrng);
	void setReqPrng(ExponentialPrng reqPrng);
	void simulate();
	double getWaitTimeMean();
	double getWaitTimeStddev();
	double getDropRatio();
	long getNReq();
	long getMaxReqQueueLength();
	void setMaxReqQueueLength(long q);
	void setNReq(long r);
}
