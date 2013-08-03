/**
 * 
 */
package me.dreilopz.swebserv.impl;

import java.util.LinkedList;

import edu.rit.numeric.ListSeries;
import edu.rit.numeric.Series;
import edu.rit.sim.Event;
import edu.rit.sim.Simulation;
import me.dreilopz.swebserv.ExponentialPrng;
import me.dreilopz.swebserv.UniformPrng;
import me.dreilopz.swebserv.WebServer;

/**
 * @author dreilopz0
 *
 */
public class PJWebServer implements WebServer {
	private double meanServiceRate;
	private double dropRatio;
	private long nReq;
	private long seed;
	private long iDrop;
	private UniformPrng uniformPrng;
	private ExponentialPrng reqPrng;
	private ExponentialPrng serverPrng;
	
	private LinkedList<Request> reqQueue;
	private long iReq;
	private Simulation sim;
	private ListSeries waitTime;
	
	private double waitTimeMean;
	private long maxReqQueueLength;
	private double waitTimeStddev;
	
	private double meanReqRate;
	private int i;
	/**
	 * @param meanReqRate the meanReqRate to set
	 */
	public void setMeanReqRate(double meanReqRate) {
		this.meanReqRate = meanReqRate;
	}

	/**
	 * @param meanServiceRate the meanServiceRate to set
	 */
	public void setMeanServiceRate(double meanServiceRate) {
		this.meanServiceRate = meanServiceRate;
	}

	/**
	 * @param nReq the nReq to set
	 */
	public void setNReq(long nReq) {
		this.nReq = nReq;
	}

	/**
	 * @param seed the seed to set
	 */
	public void setSeed(long seed) {
		this.seed = seed;
	}

	/**
	 * @param uniformPrng the uniformPrng to set
	 */
	public void setUniformPrng(UniformPrng uniformPrng) {
		this.uniformPrng = uniformPrng;
	}

	/**
	 * @param reqPrng the reqPrng to set
	 */
	public void setReqPrng(ExponentialPrng reqPrng) {
		this.reqPrng = reqPrng;
	}

	/**
	 * @param serverPrng the serverPrng to set
	 */
	public void setServerPrng(ExponentialPrng serverPrng) {
		this.serverPrng = serverPrng;
	}

	public void simulate() {
		reqQueue = new LinkedList<Request>();
		sim = new Simulation();
		generateRequest();
		waitTime = new ListSeries();
		sim.run();
		Series.Stats wt = waitTime.stats();
		this.dropRatio = ((double)iDrop) / ((double)nReq);
		this.waitTimeMean = wt.mean;
		this.waitTimeStddev = wt.stddev;
	}
	


	private void generateRequest()
	{
		Request req = new Request(++ iReq, getReqType());
		//System.out.println(req);
		addToQueue (req);
		if (iReq < nReq) {
			sim.doAfter (reqPrng.rand(), new Event()
				{
				public void perform() { generateRequest(); }
				});
			}
		}

	private void addToQueue
	(Request request)
	{
		String msg = "";
		if (reqQueue.size() < nReq)
			{
			reqQueue.add (request);
			msg += "Added";
			if (reqQueue.size() == 1) startServing();
			}
		else
			{
			msg += "Dropped";
			++ iDrop;
			}
		msg += " REQUEST " + request.toString();
		//System.out.println(msg);
	}

	private void startServing() {
		sim.doAfter (serverPrng.rand(), new Event()
			{
			public void perform() { removeFromQueue(); }
			});
	}

	private void removeFromQueue()
	{
		Request req = reqQueue.removeFirst();
		String msg = "Removed REQUEST " + req.reqNo +
					 " with waitTime " + req.waitTime();
		//System.out.println(msg);
		waitTime.add (req.waitTime());
		if (reqQueue.size() > 0) startServing();
	}

	public double getWaitTimeMean() {
		// TODO Auto-generated method stub
		return waitTimeMean;
	}

	public double getWaitTimeStddev() {
		// TODO Auto-generated method stub
		return waitTimeStddev;
	}


	public double getDropRatio() {
		// TODO Auto-generated method stub
		return dropRatio;
	}

	public void setMaxReqQueueLength(long maxReqQueueLength) {
		this.maxReqQueueLength = maxReqQueueLength;
	}
	
	class Request {
		private double startTime;
		private long reqNo;
		private int reqType;
		public Request(long reqNo, int reqType) {
			this.reqNo = reqNo;
			this.reqType = reqType;
			this.startTime = sim.time();
		}
	
		public String toString()
			{
			return "Request " + reqNo + " with type " + reqType + " with start time " + startTime;
			}
		public double waitTime()
		{
			return sim.time() - startTime;
		}
	}
	
	private int getReqType() {
		int rt = 0;
		if(i % 3 == 0) {
			rt = 0;
		} else if (i % 3 == 1) {
			rt = 1;
		} else if (i % 3 == 2) {
			rt = 2;
		}
		i++;
		return rt;
	}
}


