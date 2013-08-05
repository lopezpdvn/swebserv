package me.dreilopz.swebserv;

public interface Request {

	void setReqNo(long l);

	void setReqType(int reqType);

	void setStartTime(double time);

	double getStartTime();

	Request createInstance();

	double getArrivalTime();

	void setReqPrng(ExponentialPrng reqPrng);
}